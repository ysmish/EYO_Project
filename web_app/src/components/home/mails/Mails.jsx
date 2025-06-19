import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../../context/AuthProvider';
import '../../../styles.css';
import ActionToolbar from '../action_toolbar/ActionToolbar';

const Mails = ({mails, setMails}) => {
  const [error] = useState(null);
  const { token } = useAuth();
  const navigate = useNavigate();
  const [selected, setSelected] = useState([]);
  const location = useLocation();

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

  const toolbarActions = 
        
        
        
        
        
        selected.length > 0 ? [
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
            {mails.map(mail => (
              <div 
                key={mail.id} 
                className={`mail-item ${!mail.read ? 'unread' : ''}`}
                onClick={e => {
                  // Prevent click if clicking checkbox or delete
                  if (e.target.closest('.mail-delete-btn') || e.target.closest('.mail-select-checkbox')) return;
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
                  <span className="mail-sender">{mail.from}</span>
                  <div className="mail-text-content">
                    <span className="mail-subject">{mail.subject}</span>
                    <span className="mail-body"> - {mail.body}</span>
                  </div>
                </div>
                <div className="mail-date">{formatDateTime(mail.date)}</div>
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