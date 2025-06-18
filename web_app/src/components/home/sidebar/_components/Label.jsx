import { useState, useEffect } from 'react';
import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useAuth } from '../../../../context/AuthProvider';
import AddLabelModal from './AddLabelModal';

const Labels = ({ setSearchQuery }) => {
  const [labels, setLabels] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');
  const { token } = useAuth();

  // Fetch labels when component mounts
  useEffect(() => {
    fetchLabels();
  }, []);

  const fetchLabels = async () => {
    try {
      const response = await fetch('http://localhost:3000/api/labels', {
        headers: {
          'Authorization': token,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch labels');
      }

      const labelsData = await response.json();
      setLabels(labelsData);
    } catch (error) {
      console.error('Error fetching labels:', error);
      setError('Failed to load labels');
    }
  };

  const handleLabelClick = (labelName) => {
    setSearchQuery(`label:${labelName} `);
  };

  const openModal = () => {
    setShowModal(true);
    setError('');
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const handleLabelAdded = () => {
    // Refresh labels list when a new label is added
    fetchLabels();
  };

  return (
    <>
      <div className="labels-container">
        {/* Labels header with title and add button */}
        <div className="labels-header">
          <span className="labels-title">Labels</span>
          <button 
            className="add-label-btn"
            onClick={openModal}
            title="Add new label"
          >
            <i className="bi bi-plus"></i>
          </button>
        </div>

        {/* Error message */}
        {error && (
          <div className="labels-error">
            <i className="bi bi-exclamation-triangle"></i>
            {error}
          </div>
        )}

        {/* Labels list */}
        <div className="labels-list">
          {labels.map((label) => (
            <div 
              key={label.id} 
              className="side-item label-item"
              onClick={() => handleLabelClick(label.name)}
            >
              <div 
                className="label-color-dot"
                style={{ backgroundColor: label.color || '#4F46E5' }}
              />
              <span>{label.name}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Add Label Modal */}
      {showModal && (
        <AddLabelModal
          onClose={closeModal}
          onLabelAdded={handleLabelAdded}
        />
      )}
    </>
  );
};

export default Labels;