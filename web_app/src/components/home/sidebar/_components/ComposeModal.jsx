import { useState } from 'react';
import { useAuth } from '../../../../context/AuthProvider';
import '../../../../styles.css';

const ComposeModal = ({ onClose }) => {
  const { token } = useAuth();
  const [formData, setFormData] = useState({
    to: '',
    cc: '',
    subject: '',
    body: '',
    attachments: []
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [showCC, setShowCC] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSend = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    // Basic validation
    if (!formData.to || !formData.subject || !formData.body) {
      setError('Please fill in all required fields (To, Subject, Body)');
      setIsLoading(false);
      return;
    }

    try {
      const payload = {
        to: formData.to ? formData.to.split(',').map(user => user.trim()).filter(user => user) : [],
        subject: formData.subject.trim(),
        body: formData.body.trim(),
        cc: formData.cc ? formData.cc.split(',').map(user => user.trim()).filter(user => user) : [],
        attachments: formData.attachments
      };

      const response = await fetch('http://localhost:3000/api/mails', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        setSuccess(true);
        setTimeout(() => {
          onClose();
        }, 1500);
      } else {
        const errorData = await response.json();
        setError(errorData.error || 'Failed to send mail');
      }
    } catch (err) {
      setError('Network error. Please try again.');
      console.error('Error sending mail:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleModalClick = (e) => {
    // Close modal when clicking outside (only when expanded)
    if (e.target === e.currentTarget && isExpanded) {
      onClose();
    }
  };

  const toggleCC = () => {
    setShowCC(!showCC);
    if (!showCC) {
      setFormData(prev => ({ ...prev, cc: '' }));
    }
  };

  const toggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  if (success) {
    return (
      <div className={`compose-modal-overlay ${isExpanded ? 'expanded' : ''}`} onClick={handleModalClick}>
        <div className={`compose-modal ${isExpanded ? 'expanded' : ''}`}>
          <div className="compose-success">
            <i className="bi bi-check-circle-fill"></i>
            <p>Mail sent successfully!</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={`compose-modal-overlay ${isExpanded ? 'expanded' : ''}`} onClick={handleModalClick}>
      <div className={`compose-modal ${isExpanded ? 'expanded' : ''}`}>
        <div className="compose-header">
          <span>New Message</span>
          <div className="compose-header-actions">
            <button 
              type="button" 
              className="compose-btn-icon" 
              onClick={toggleExpand}
              title={isExpanded ? "Collapse" : "Expand"}
            >
              <i className={`bi ${isExpanded ? 'bi-fullscreen-exit' : 'bi-fullscreen'}`}></i>
            </button>
            <button 
              type="button" 
              className="compose-btn-icon" 
              onClick={onClose}
              title="Close"
            >
              <i className="bi bi-x"></i>
            </button>
          </div>
        </div>

        <form onSubmit={handleSend} className="compose-form">
          <div className="compose-fields-container">
            <div className="compose-field-inline">
              <div className="compose-field-left">
                <span className="compose-label">To</span>
              </div>
              <input
                type="text"
                name="to"
                value={formData.to}
                onChange={handleChange}
                required
                className="compose-input-inline"
              />
              <span 
                className={`compose-cc-toggle ${showCC ? 'active' : ''}`}
                onClick={toggleCC}
              >
                Cc
              </span>
            </div>

            {showCC && (
              <div className="compose-field-inline">
                <div className="compose-field-left">
                  <span className="compose-label">Cc</span>
                </div>
                <input
                  type="text"
                  name="cc"
                  value={formData.cc}
                  onChange={handleChange}
                  className="compose-input-inline"
                />
              </div>
            )}

            <div className="compose-field-inline">
              <input
                type="text"
                name="subject"
                value={formData.subject}
                onChange={handleChange}
                placeholder="Subject"
                required
                className="compose-input-subject"
              />
            </div>
          </div>

          <div className="compose-body-container">
            <textarea
              name="body"
              value={formData.body}
              onChange={handleChange}
              required
              className="compose-body-textarea"
            />
          </div>

          {error && (
            <div className="compose-error">
              {error}
            </div>
          )}

          <div className="compose-actions">
            <button 
              type="submit" 
              className="compose-btn-send"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <i className="bi bi-arrow-clockwise"></i>
                  Sending...
                </>
              ) : (
                <>
                  <i className="bi bi-send"></i>
                  Send
                </>
              )}
            </button>
            <button 
              type="button" 
              className="compose-btn-cancel"
              onClick={onClose}
              disabled={isLoading}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ComposeModal; 