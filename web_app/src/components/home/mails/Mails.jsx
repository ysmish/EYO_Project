import { useState } from 'react';
import '../../../styles.css';

const Mails = ({mails, setMails}) => {
  const [error] = useState(null);

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
      <h2>Your Mails</h2>
      {mails.length === 0 ? (
        <p>No mails found.</p>
      ) : (
        <div className="mails-list">
          {mails.map(mail => (
            <div key={mail.id} className={`mail-item ${!mail.read ? 'unread' : ''}`}>
              <div className="mail-content">
                <span className="mail-sender">{mail.from}</span>
                                  <div className="mail-text-content">
                    <span className="mail-subject">{mail.subject}</span>
                    <span className="mail-body">&nbsp; - {mail.body}</span>
                  </div>
              </div>
              <div className="mail-date">{formatDateTime(mail.date)}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Mails;