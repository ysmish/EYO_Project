import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../context/AuthProvider';
import { useParams, useNavigate } from 'react-router-dom';
import '../../../styles.css';
import ActionToolbar from '../action_toolbar/ActionToolbar';

const Mail = () => {
  const { mailId, searchString } = useParams();
  const [mail, setMail] = useState(null);
  const [fromPhoto, setFromPhoto] = useState(null);
  const [labels, setLabels] = useState([]);
  const [availableLabels, setAvailableLabels] = useState([]);
  const [showLabelDropdown, setShowLabelDropdown] = useState(false);
  const [labelButtonPosition, setLabelButtonPosition] = useState(null);
  const navigate = useNavigate();
  const { token } = useAuth();

  useEffect(() => {
    const fetchMail = async () => {
      try {
        const response = await fetch(`http://localhost:3000/api/mails/${mailId}`, {
          headers: {
            'Authorization': `${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch mail');
        }
        const data = await response.json();
        setMail(data);
      } catch (error) {
        console.error('Error fetching mail:', error);
      }
    };
    if (!token) {
      navigate('/login');
    } else {
      fetchMail();
    }
  }, [token, navigate, mailId]);

  const handleBack = () => {
    // Navigate back to the search results if we have a search string
    if (searchString && searchString !== 'in%3Aall') {
      navigate(`/search/${searchString}`);
    } else {
      // Navigate to all mails search if searchString is 'in:all' or not provided
      navigate('/search/in%3Aall');
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await fetch(`http://localhost:3000/api/users/${mail.from}`, {
          headers: {
            Authorization: ` ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch user data");
        }

        const userData = await response.json();
        setFromPhoto(userData.photo);
        // You can set user data in context or state if needed
      } catch (error) {
        console.error("Error fetching user data:", error);
        // Optionally handle the error, e.g., show a notification
      }
    };
    if (!token) {
      navigate('/login');
    } else {
      fetchUser();
    }
  }, [token, navigate, mail, setFromPhoto]);

  useEffect(() => {
    const fetchLabels = async () => {
      try {
        const response = await fetch(`http://localhost:3000/api/labels`, {
          headers: {
            'Authorization': `${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch labels');
        }
        const data = await response.json();
        for (const label of mail.labels || []) {
          const labelData = data.find(l => l.id === label);
          if (labelData) {
            setLabels(prevLabels => [...prevLabels, labelData.name]);
          } else {
            setLabels(prevLabels => [...prevLabels, label]); // Fallback to label ID if not found
          }
          setLabels(prevLabels => [...new Set(prevLabels)]); // Remove duplicates
        }
      } catch (error) {
        console.error('Error fetching labels:', error);
      }
    };
    if (token) {
      fetchLabels();
    }
  }, [token, mail]);

  // Fetch available labels for the dropdown
  useEffect(() => {
    const fetchAvailableLabels = async () => {
      try {
        const response = await fetch(`http://localhost:3000/api/labels`, {
          headers: {
            'Authorization': `${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch available labels');
        }
        const data = await response.json();
        setAvailableLabels(data);
      } catch (error) {
        console.error('Error fetching available labels:', error);
      }
    };
    if (token) {
      fetchAvailableLabels();
    }
  }, [token]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (!event.target.closest('.label-dropdown') && !event.target.closest('.action-toolbar-btn')) {
        setShowLabelDropdown(false);
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

  if (!mail) {
    return (
      <div className="mail-view-container">
        <div className="mail-view-card">
          <button className="mail-back-btn" onClick={handleBack}>← Back to Mails</button>
          <div className="mail-loading">Loading...</div>
        </div>
      </div>
    );
  }

  const handleDelete = async () => {
    await fetch(`http://localhost:3000/api/mails/${mailId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': token
      }
    });
    navigate('/mails');
  };

  const handleStar = async () => {
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
        setMail(prev => ({
          ...prev,
          labels: newLabels
        }));
        // Update labels display
        setLabels(prevLabels => {
          const updatedLabels = isStarred
            ? prevLabels.filter(label => label !== 'Starred')
            : [...new Set([...prevLabels, 'Starred'])];
          return updatedLabels;
        });
      }
    } catch (error) {
      console.error('Error updating star status:', error);
    }
  };

  const handleAddToLabel = async (labelName) => {
    // Find the label by name to get its ID
    const label = availableLabels.find(l => l.name === labelName);
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
          setMail(prev => ({
            ...prev,
            labels: newLabels
          }));
          // Update labels display
          setLabels(prevLabels => [...new Set([...prevLabels, labelName])]);
        }
      } catch (error) {
        console.error('Error adding label:', error);
      }
    }
    setShowLabelDropdown(false);
    setLabelButtonPosition(null);
  };

  const handleRemoveLabel = async (labelId) => {
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
        setMail(prev => ({
          ...prev,
          labels: newLabels
        }));
        // Update labels display
        const labelName = availableLabels.find(l => l.id === labelId)?.name;
        if (labelName) {
          setLabels(prevLabels => prevLabels.filter(label => label !== labelName));
        }
      }
    } catch (error) {
      console.error('Error removing label:', error);
    }
    setShowLabelDropdown(false);
    setLabelButtonPosition(null);
  };

  // Helper function to check if a label is applied to the mail
  const isLabelApplied = (labelId) => {
    return mail.labels && mail.labels.includes(labelId);
  };

  const isStarred = mail.labels && mail.labels.includes('Starred');
  const toolbarActions = [
    {
      key: 'star',
      iconClass: isStarred ? 'bi bi-star-fill' : 'bi bi-star',
      label: isStarred ? 'Unstar' : 'Star',
      onClick: handleStar
    },
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
        setShowLabelDropdown(true);
      }
    },
    {
      key: 'delete',
      iconClass: 'bi bi-trash',
      label: 'Delete',
      onClick: handleDelete
    }
  ];
  return (
    <>
      <div className="mail-view-container">
        <div className="mail-view-card">
          <ActionToolbar actions={toolbarActions} />
          <div className="mail-view-header">
            <button className="mail-back-btn" onClick={handleBack}>← Back</button>
            <div className="mail-view-header-main">
              <div className="mail-view-subject">{mail.subject}</div>
              {mail.labels && mail.labels.length > 0 && (
                <div className="mail-view-labels">
                  {labels.map((label, idx) => (
                    <span className="mail-label-chip" key={idx}>{label}</span>
                  ))}
                </div>
              )}
            </div>
            <div className="mail-view-date">{new Date(mail.date).toLocaleString()}</div>
          </div>
          <div className="mail-view-meta">
            <div className="mail-view-from">
              {fromPhoto && (
                <img
                  src={fromPhoto}
                  alt="From user"
                  className="mail-from-photo"
                  style={{
                    width: '36px',
                    height: '36px',
                    borderRadius: '50%',
                    objectFit: 'cover',
                    marginRight: '0.75rem',
                    verticalAlign: 'middle',
                    border: '1px solid var(--shadow-color)'
                  }}
                />
              )}
            </div>
            <span className="mail-label">From: {mail.from}</span>
            <div className="mail-view-to">
              <span className="mail-label">To:</span> {Array.isArray(mail.to) ? mail.to.join(', ') : mail.to}
            </div>
            {mail.cc && mail.cc.length > 0 && (
              <div className="mail-view-cc">
                <span className="mail-label">Cc:</span> {mail.cc.join(', ')}
              </div>
            )}
          </div>
          <div className="mail-view-body mail-view-body-indent">
            {mail.body}
          </div>
          {mail.attachments && mail.attachments.length > 0 && (
            <div className="mail-view-attachments">
              <span className="mail-label">Attachments:</span>
              <ul>
                {mail.attachments.map((att, idx) => (
                  <li key={idx}>
                    <a href={att.url} target="_blank" rel="noopener noreferrer">{att.name}</a>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>
      {showLabelDropdown && (
        <div 
          className="label-dropdown"
          style={labelButtonPosition ? {
            position: 'absolute',
            top: `${labelButtonPosition.top}px`,
            left: `${labelButtonPosition.left}px`
          } : {}}
        >
          {availableLabels.map(label => (
            <button
              key={label.id}
              className={`label-dropdown-item ${isLabelApplied(label.id) ? 'applied' : ''}`}
              onClick={() => {
                if (isLabelApplied(label.id)) {
                  handleRemoveLabel(label.id);
                } else {
                  handleAddToLabel(label.name);
                }
              }}
            >
              <div 
                className="label-color-dot"
                style={{ backgroundColor: label.color || '#4F46E5' }}
              />
              {label.name}
              {isLabelApplied(label.id) && (
                <i className="bi bi-check"></i>
              )}
            </button>
          ))}
        </div>
      )}
    </>
  );
};

export default Mail;