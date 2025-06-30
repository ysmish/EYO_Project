import { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../../../context/AuthProvider';
import '../../../../styles.css';

const AddLabelModal = ({ onClose, onLabelAdded }) => {
  const { token } = useAuth();
  const [newLabelName, setNewLabelName] = useState('');
  const [selectedColor, setSelectedColor] = useState('#4F46E5');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const modalRef = useRef(null);
  const inputRef = useRef(null);

  // Predefined color options
  const colorOptions = [
    '#4F46E5', // Blue
    '#EF4444', // Red
    '#10B981', // Green
    '#F59E0B', // Yellow
    '#8B5CF6', // Purple
    '#F97316', // Orange
    '#06B6D4', // Cyan
    '#84CC16', // Lime
    '#EC4899', // Pink
    '#6B7280', // Gray
    '#DC2626', // Dark Red
    '#059669'  // Dark Green
  ];

  // Focus input when modal opens
  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, []);

  // Handle click outside modal to close
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        onClose();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClose]);

  const handleAddLabel = async () => {
    if (!newLabelName.trim()) {
      setError('Label name cannot be empty');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:3000/api/labels', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token,
        },
        body: JSON.stringify({ 
          name: newLabelName.trim(),
          color: selectedColor
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to create label');
      }

      // Notify parent component that label was added
      if (onLabelAdded) {
        onLabelAdded();
      }
      onClose();
    } catch (error) {
      console.error('Error creating label:', error);
      setError(error.message);
    } finally {
      setLoading(false);
      window.location.reload();
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleAddLabel();
    } else if (e.key === 'Escape') {
      onClose();
    }
  };

  return (
    <div className="label-modal-overlay">
      <div 
        className="label-modal" 
        ref={modalRef}
      >
        <div className="label-modal-header">
          <h3>Add New Label</h3>
          <button 
            className="label-modal-close"
            onClick={onClose}
            title="Close"
          >
            <i className="bi bi-x"></i>
          </button>
        </div>
        
        <div className="label-modal-content">
          <div className="label-modal-field">
            <label htmlFor="labelName">Label Name</label>
            <input
              id="labelName"
              ref={inputRef}
              type="text"
              className="label-modal-input"
              placeholder="Enter label name"
              value={newLabelName}
              onChange={(e) => setNewLabelName(e.target.value)}
              onKeyDown={handleKeyPress}
            />
          </div>

          <div className="label-modal-field">
            <label htmlFor="labelColor">Color</label>
            <div className="color-picker-container">
              <div className="color-options">
                {colorOptions.map((color) => (
                  <button
                    key={color}
                    type="button"
                    className={`color-option ${selectedColor === color ? 'selected' : ''}`}
                    style={{ backgroundColor: color }}
                    onClick={() => setSelectedColor(color)}
                    title={`Select ${color}`}
                  />
                ))}
              </div>
              <div className="color-preview">
                <span>Selected: </span>
                <div 
                  className="color-preview-circle"
                  style={{ backgroundColor: selectedColor }}
                />
              </div>
            </div>
          </div>

          {/* Error message */}
          {error && (
            <div className="label-modal-error">
              <i className="bi bi-exclamation-triangle"></i>
              {error}
            </div>
          )}
        </div>

        <div className="label-modal-actions">
          <button 
            className="label-modal-btn label-modal-btn-cancel"
            onClick={onClose}
            disabled={loading}
          >
            Cancel
          </button>
          <button 
            className="label-modal-btn label-modal-btn-save"
            onClick={handleAddLabel}
            disabled={loading || !newLabelName.trim()}
          >
            {loading ? (
              <>
                <i className="bi bi-arrow-clockwise loading-spin"></i>
                Adding...
              </>
            ) : (
              'Add Label'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddLabelModal; 