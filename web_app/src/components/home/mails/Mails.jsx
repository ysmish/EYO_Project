import { useState } from 'react';
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
  const location = useLocation();
  const { onOpenCompose } = useOutletContext();

   const totalPages = Math.ceil(mails.length / PAGE_SIZE);
   const pagedMails = mails.slice(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE);
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

  const toolbarActions = selected.length > 0 ? [
    {
      key: 'star',
      iconClass: allSelectedStarred ? 'bi bi-star-fill' : 'bi bi-star',
      label: allSelectedStarred ? 'Unstar' : 'Star',
      onClick: handleStar
    },
    {
      key: 'delete',
      iconClass: 'bi bi-trash',
      label: 'Delete',
      onClick: handleDelete
    }
    // Future actions can be added here
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
      {toolbarActions.length > 0 && <ActionToolbar actions={toolbarActions} />}
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
            <h2>Your Mails</h2>
          </div>
          <div className="mails-items-container">
            {pagedMails.map(mail => (
              <div 
                key={mail.id} 
                className={`mail-item ${!mail.read ? 'unread' : ''}`}
                onClick={e => {
                  // Prevent click if clicking checkbox, star button, or delete button
                  if (e.target.closest('.mail-delete-btn') || e.target.closest('.mail-star-btn') || e.target.closest('.mail-select-checkbox')) return;
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
                  className="mail-star-btn"
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
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Mails;