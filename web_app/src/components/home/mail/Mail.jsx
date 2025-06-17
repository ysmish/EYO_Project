import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../../styles.css';

const Mail = () => {
  const { mailId } = useParams();
  const navigate = useNavigate();

  const handleBack = () => {
    navigate('/mails');
  };

  return (
    <div>
      <button onClick={handleBack}>
        ← Back to Mails
      </button>
      <h2>Mail ID: {mailId}</h2>
      <p>Mail content will be implemented later...</p>
    </div>
  );
};

export default Mail;