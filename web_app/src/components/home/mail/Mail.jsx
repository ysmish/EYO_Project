import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthProvider';
import '../../../styles.css';
import ActionToolbar from '../action_toolbar/ActionToolbar';

const Mail = () => {
  const { mailId } = useParams();
  const navigate = useNavigate();
  const { token } = useAuth();

  const handleBack = () => {
    navigate('/mails');
  };

  const handleDelete = async () => {
    await fetch(`http://localhost:3000/api/mails/${mailId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': token
      }
    });
    navigate('/mails');
  };

  const toolbarActions = [
    {
      key: 'delete',
      iconClass: 'bi bi-trash',
      label: 'Delete',
      onClick: handleDelete
    }
    // Future actions can be added here
  ];

  return (
    <div>
      <ActionToolbar actions={toolbarActions} />
      <button onClick={handleBack}>
        ← Back to Mails
      </button>
      <h2>Mail ID: {mailId}</h2>
      <p>Mail content will be implemented later...</p>
    </div>
  );
};

export default Mail;