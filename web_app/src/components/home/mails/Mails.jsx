import { useState, useEffect } from 'react';
import { useNavigate, useLocation, useOutletContext } from 'react-router-dom';
import { useAuth } from '../../../context/AuthProvider';
import '../../../styles.css';
import ActionToolbar from '../action_toolbar/ActionToolbar';

const PAGE_SIZE = 50; // Number of mails per page

const Mails = ({mails, setMails}) => {
  const [error] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const { token } = useAuth();
  const navigate = useNavigate();
  const [selected, setSelected] = useState([]);
  const [showLabelDropdown, setShowLabelDropdown] = useState(null);
  const [labelButtonPosition, setLabelButtonPosition] = useState(null);
  const [labels, setLabels] = useState([]);
  const location = useLocation();
  const { onOpenCompose } = useOutletContext();
  const totalPages = Math.ceil(mails.length / PAGE_SIZE);
  const pagedMails = mails.slice(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE);

  // Fetch labels on component mount
  useEffect(() => {
    const fetchLabels = async () => {
      try {
        const response = await fetch('http://localhost:3000/api/labels', {
          headers: {
            'Authorization': token
          }
        });
        if (response.ok) {
          const data = await response.json();
          setLabels(data);
        }
      } catch (error) {
        console.error('Error fetching labels:', error);
      }
    };
    if (token) {
      fetchLabels();
    }
  }, [token]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest('.label-dropdown') && !event.target.closest('.mail-label-btn') && !event.target.closest('.action-toolbar-btn')) {
        setShowLabelDropdown(null);
        setLabelButtonPosition(null);
      }
    };

    if (showLabelDropdown) {
      document.addEventListener('mousedown', handleClickOutside);
      return () => {
        document.removeEventListener('mousedown', handleClickOutside);
      };
    }
  }, [showLabelDropdown]);

  // Determine the current search context
  const getCurrentSearchString = () => {
    const path = location.pathname;
    if (path === '/mails') {
      // This should rarely happen due to redirect, but fallback to 'all'
      return 'in%3Aall';
    } else if (path.startsWith('/search/')) {
      const searchQuery = path.split('/search/')[1];
      return searchQuery || 'in%3Aall';
    }
    return 'in%3Aall';
  };

  // Check if we're currently viewing the sent folder
  const isInSentFolder = () => {
    const path = location.pathname;
    if (path.startsWith('/search/')) {
      const searchQuery = path.split('/search/')[1];
      if (searchQuery) {
        try {
          const decodedQuery = decodeURIComponent(searchQuery.split('/')[0]).trim();
          return decodedQuery === 'in:sent';
        } catch (e) {
          return false;
        }
      }
    }
    return false;
  };

  // Get display text for mail sender/recipient
  const getMailDisplayText = (mail) => {
    // If it's a draft, show "Draft"
    if (mail.labels && mail.labels.includes('Drafts')) {
      return 'Draft';
    }
    
    // If we're in sent folder, show "To: [recipients]"
    if (isInSentFolder()) {
      const recipients = Array.isArray(mail.to) ? mail.to.join(', ') : mail.to;
      return `To: ${recipients}`;
    }
    
    // Otherwise show sender
    return mail.from;
  };

  // Get CSS class for mail sender/recipient
  const getMailSenderClass = (mail) => {
    if (mail.labels && mail.labels.includes('Drafts')) {
      return 'mail-sender mail-sender--draft';
    }
    return 'mail-sender';
  };

  const formatDateTime = (dateString) => {
    const mailDate = new Date(dateString);
    const today = new Date();
    const isToday = mailDate.toDateString() === today.toDateString();
    
    if (isToday) {
      return mailDate.toLocaleTimeString('en-US', { 
        hour: 'numeric', 
        minute: '2-digit',
        hour12: true 
      });
    } else {
      return mailDate.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric',
        year: mailDate.getFullYear() !== today.getFullYear() ? 'numeric' : undefined
      });
    }
  };

  const markAsRead = async (mailId) => {
    try {
      const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        },
        body: JSON.stringify({ read: true })
      });

      if (response.ok) {
        // Update the local state to mark the mail as read
        setMails(prevMails => 
          prevMails.map(mail => 
            mail.id === mailId ? { ...mail, read: true } : mail
          )
        );
      } else {
        console.error('Failed to mark mail as read');
      }
    } catch (error) {
      console.error('Error marking mail as read:', error);
    }
  };

  const handleMailClick = (mail) => {
    // If it's a draft, open compose modal for editing
    if (mail.labels && mail.labels.includes('Drafts')) {
      onOpenCompose(mail);
      return;
    }

    // Only mark as read if it's currently unread
    if (!mail.read) {
      markAsRead(mail.id);
    }
    
    // Navigate to the mail detail page with search context
    const searchString = getCurrentSearchString();
    navigate(`/search/${searchString}/${mail.id}`);
  };

  const handleSelect = (mailId) => {
    setSelected(prev => prev.includes(mailId) ? prev.filter(id => id !== mailId) : [...prev, mailId]);
  };

  const handleDelete = async () => {
    for (const mailId of selected) {
      await fetch(`http://localhost:3000/api/mails/${mailId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': token
        }
      });
    }
    setMails(prev => prev.filter(mail => !selected.includes(mail.id)));
    setSelected([]);
  };

  // Determine if all selected mails are starred
  const allSelectedStarred = selected.length > 0 && selected.every(id => {
    const mail = mails.find(m => m.id === id);
    return mail && mail.labels && mail.labels.includes('Starred');
  });

  const handleStar = async () => {
    const updatedMails = [...mails];
    
    for (const mailId of selected) {
      const mailIndex = updatedMails.findIndex(m => m.id === mailId);
      if (mailIndex !== -1) {
        const mail = updatedMails[mailIndex];
        const isStarred = mail.labels && mail.labels.includes('Starred');
        const newLabels = isStarred
          ? (mail.labels || []).filter(label => label !== 'Starred')
          : [...(mail.labels || []), 'Starred'];

        try {
          const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
            method: 'PATCH',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': token
            },
            body: JSON.stringify({ labels: newLabels })
          });

          if (response.ok) {
            updatedMails[mailIndex] = { ...mail, labels: newLabels };
          }
        } catch (error) {
          console.error('Error updating star status:', error);
        }
      }
    }
    
    setMails(updatedMails);
    setSelected([]);
  };

  const handleAddToLabel = async (mailId, labelName) => {
    const mailIndex = mails.findIndex(m => m.id === mailId);
    if (mailIndex !== -1) {
      const mail = mails[mailIndex];
      // Find the label by name to get its ID
      const label = labels.find(l => l.name === labelName);
      if (label) {
        const newLabels = [...(mail.labels || []), label.id];

        try {
          const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
            method: 'PATCH',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': token
            },
            body: JSON.stringify({ labels: newLabels })
          });

          if (response.ok) {
            setMails(prevMails =>
              prevMails.map(m =>
                m.id === mailId ? { ...m, labels: newLabels } : m
              )
            );
          }
        } catch (error) {
          console.error('Error adding label:', error);
        }
      }
    }
    setShowLabelDropdown(null);
    setLabelButtonPosition(null);
  };

  const handleBulkAddToLabel = async (labelName) => {
    const updatedMails = [...mails];
    // Find the label by name to get its ID
    const label = labels.find(l => l.name === labelName);
    
    if (label) {
      for (const mailId of selected) {
        const mailIndex = updatedMails.findIndex(m => m.id === mailId);
        if (mailIndex !== -1) {
          const mail = updatedMails[mailIndex];
          const newLabels = [...(mail.labels || []), label.id];

          try {
            const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
              method: 'PATCH',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': token
              },
              body: JSON.stringify({ labels: newLabels })
            });

            if (response.ok) {
              updatedMails[mailIndex] = { ...mail, labels: newLabels };
            }
          } catch (error) {
            console.error('Error adding label:', error);
          }
        }
      }
      
      setMails(updatedMails);
      setSelected([]);
    }
  };

  const handleRemoveLabel = async (mailId, labelId) => {
    const mailIndex = mails.findIndex(m => m.id === mailId);
    if (mailIndex !== -1) {
      const mail = mails[mailIndex];
      const newLabels = (mail.labels || []).filter(label => label !== labelId);

      try {
        const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': token
          },
          body: JSON.stringify({ labels: newLabels })
        });

        if (response.ok) {
          setMails(prevMails =>
            prevMails.map(m =>
              m.id === mailId ? { ...m, labels: newLabels } : m
            )
          );
        }
      } catch (error) {
        console.error('Error removing label:', error);
      }
    }
  };

  // Helper function to get label name by ID
  const getLabelNameById = (labelId) => {
    const label = labels.find(l => l.id === labelId);
    return label ? label.name : labelId;
  };

  // Helper function to get label color by ID
  const getLabelColorById = (labelId) => {
    const label = labels.find(l => l.id === labelId);
    return label ? label.color : '#4F46E5';
  };

  // Helper function to check if a label is applied to a mail
  const isLabelApplied = (mail, labelId) => {
    return mail.labels && mail.labels.includes(labelId);
  };

  const toolbarActions = selected.length > 0 ? [
    {
      key: 'label',
      iconClass: 'bi bi-tag',
      label: 'Add to Label',
      onClick: (event) => {
        const rect = event.currentTarget.getBoundingClientRect();
        setLabelButtonPosition({
          top: rect.bottom + window.scrollY,
          left: rect.left + window.scrollX
        });
        setShowLabelDropdown('bulk');
      }
    },
    {
      key: 'delete',
      iconClass: 'bi bi-trash',
      label: 'Delete',
      onClick: handleDelete
    }
  ] : [];

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button onClick={() => window.location.reload()}>Try Again</button>
      </div>
    );
  }

  return (
    <div className="mails-container">
      {mails.length > 0 && (
        <div className="pagination-bar" style={{ display: 'flex', alignItems: 'center', marginBottom: 10 }}>
          <button
            onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
            disabled={currentPage === 0}
            style={{ marginRight: 8 }}
          >
            &lt;
          </button>
          <span>
            {currentPage * PAGE_SIZE + 1} - {Math.min((currentPage + 1) * PAGE_SIZE, mails.length)} of {mails.length}
          </span>
          <button
            onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))}
            disabled={currentPage >= totalPages - 1}
            style={{ marginLeft: 8 }}
          >
            &gt;
          </button>
          {/* Quick range select like Gmail */}
          <select
            value={currentPage}
            onChange={e => setCurrentPage(Number(e.target.value))}
            style={{ marginLeft: 16 }}
          >
            {Array.from({ length: totalPages }).map((_, idx) => (
              <option key={idx} value={idx}>
                {idx * PAGE_SIZE + 1} - {Math.min((idx + 1) * PAGE_SIZE, mails.length)}
              </option>
            ))}
          </select>
        </div>
      )}
      {mails.length === 0 ? (
        <div className="mails-list">
          <div className="mails-header">
            <h2>Your Mails</h2>
          </div>
          <div className="no-mails-message">
            <p>No mails found.</p>
          </div>
        </div>
      ) : (
        <div className="mails-list">
          <div className="mails-header">
            {toolbarActions.length > 0 ? <ActionToolbar actions={toolbarActions} /> : <h2>Your Mails</h2>}
          </div>
          <div className="mails-items-container">
            {pagedMails.map(mail => (
              <div 
                key={mail.id} 
                className={`mail-item ${!mail.read ? 'unread' : ''}`}
                onClick={e => {
                  // Prevent click if clicking checkbox, star button, label button, or delete button
                  if (e.target.closest('.mail-delete-btn') || e.target.closest('.mail-star-btn') || e.target.closest('.mail-label-btn') || e.target.closest('.mail-select-checkbox')) return;
                  handleMailClick(mail);
                }}
              >
                <input
                  type="checkbox"
                  className="mail-select-checkbox"
                  checked={selected.includes(mail.id)}
                  onChange={() => handleSelect(mail.id)}
                  onClick={e => e.stopPropagation()}
                />
                <button
                  className="mail-star-btn"
                  style={{ backgroundColor: '10px' }}
                  title={mail.labels && mail.labels.includes('Starred') ? 'Unstar' : 'Star'}
                  onClick={async e => {
                    e.stopPropagation();
                    const isStarred = mail.labels && mail.labels.includes('Starred');
                    const newLabels = isStarred
                      ? (mail.labels || []).filter(label => label !== 'Starred')
                      : [...(mail.labels || []), 'Starred'];

                    try {
                      const response = await fetch(`http://localhost:3000/api/mails/${mail.id}`, {
                        method: 'PATCH',
                        headers: {
                          'Content-Type': 'application/json',
                          'Authorization': token
                        },
                        body: JSON.stringify({ labels: newLabels })
                      });

                      if (response.ok) {
                        setMails(prevMails =>
                          prevMails.map(m =>
                            m.id === mail.id ? { ...m, labels: newLabels } : m
                          )
                        );
                      }
                    } catch (error) {
                      console.error('Error updating star status:', error);
                    }
                  }}
                >
                  <i className={`bi ${mail.labels && mail.labels.includes('Starred') ? 'bi-star-fill' : 'bi-star'}`}></i>
                </button>
                <div className="mail-content">
                  <span className={getMailSenderClass(mail)}>
                    {getMailDisplayText(mail)}
                  </span>
                  <div className="mail-text-content">
                    <span className="mail-subject">{mail.subject}</span>
                    <span className="mail-body"> - {mail.body}</span>
                  </div>
                </div>
                <div className="mail-date">{formatDateTime(mail.date)}</div>
                <button
                  className="mail-label-btn"
                  title="Add to Label"
                  onClick={e => {
                    e.stopPropagation();
                    setShowLabelDropdown(mail.id);
                  }}
                >
                  <i className="bi bi-tag"></i>
                </button>
                <button
                  className="mail-delete-btn"
                  title="Delete"
                  onClick={async e => {
                    e.stopPropagation();
                    await fetch(`http://localhost:3000/api/mails/${mail.id}`, {
                      method: 'DELETE',
                      headers: {
                        'Authorization': token
                      }
                    });
                    setMails(prev => prev.filter(m => m.id !== mail.id));
                    setSelected(prev => prev.filter(id => id !== mail.id));
                  }}
                >
                  <i className="bi bi-trash"></i>
                </button>
                {showLabelDropdown === mail.id && (
                  <div className="label-dropdown">
                    {labels.map(label => (
                      <button
                        key={label.id}
                        className={`label-dropdown-item ${isLabelApplied(mail, label.id) ? 'applied' : ''}`}
                        onClick={() => {
                          if (isLabelApplied(mail, label.id)) {
                            handleRemoveLabel(mail.id, label.id);
                          } else {
                            handleAddToLabel(mail.id, label.name);
                          }
                        }}
                      >
                        <div 
                          className="label-color-dot"
                          style={{ backgroundColor: label.color || '#4F46E5' }}
                        />
                        {label.name}
                        {isLabelApplied(mail, label.id) && (
                          <i className="bi bi-check"></i>
                        )}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
      {showLabelDropdown === 'bulk' && (
        <div 
          className="label-dropdown bulk-label-dropdown"
          style={labelButtonPosition ? {
            position: 'absolute',
            top: `${labelButtonPosition.top}px`,
            left: `${labelButtonPosition.left}px`
          } : {}}
        >
          {labels.map(label => {
            // Check if all selected emails have this label
            const allSelectedHaveLabel = selected.length > 0 && selected.every(mailId => {
              const mail = mails.find(m => m.id === mailId);
              return mail && isLabelApplied(mail, label.id);
            });
            
            return (
              <button
                key={label.id}
                className={`label-dropdown-item ${allSelectedHaveLabel ? 'applied' : ''}`}
                onClick={() => {
                  if (allSelectedHaveLabel) {
                    // Remove label from all selected emails
                    selected.forEach(mailId => handleRemoveLabel(mailId, label.id));
                  } else {
                    // Add label to all selected emails
                    handleBulkAddToLabel(label.name);
                  }
                }}
              >
                <div 
                  className="label-color-dot"
                  style={{ backgroundColor: label.color || '#4F46E5' }}
                />
                {label.name}
                {allSelectedHaveLabel && (
                  <i className="bi bi-check"></i>
                )}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Mails;