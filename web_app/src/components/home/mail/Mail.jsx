import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../context/AuthProvider';
import { useParams, useNavigate } from 'react-router-dom';
import '../../../styles.css';

const Mail = () => {
  const { mailId } = useParams();
  const [mail, setMail] = useState(null);
  const [fromPhoto, setFromPhoto] = useState(null);
  const [labels, setLabels] = useState([]);
  const { token } = useAuth();
  const navigate = useNavigate();

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
    navigate('/mails');
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

  return (
    <div className="mail-view-container">
      <div className="mail-view-card">
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
  );
};

export default Mail;