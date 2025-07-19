import { useState, useEffect, useRef, useCallback } from 'react';
import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useAuth } from '../../../../context/AuthProvider';
import { useNavigate } from 'react-router-dom';
import AddLabelModal from './AddLabelModal';

const Labels = ({ activeSection, collapsed }) => {
  const navigate = useNavigate();
  const [labels, setLabels] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');
  const [showDropdown, setShowDropdown] = useState(null);
  const [dropdownPosition, setDropdownPosition] = useState({ top: 0, left: 0 });
  const [showColorModal, setShowColorModal] = useState(null);
  const [showNameModal, setShowNameModal] = useState(null);
  const [nameInput, setNameInput] = useState('');
  const { token } = useAuth();
  const dropdownRef = useRef(null);

  // Color options for the color picker
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

  const fetchLabels = useCallback(async () => {
    try {
      const response = await fetch('http://localhost:3000/api/labels', {
        headers: {
          'Authorization': token,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch labels');
      }

      const labelsData = await response.json()
      setLabels(labelsData.filter(label => label.id > 5)); // Exclude default labels (1-5)
    } catch (error) {
      console.error('Error fetching labels:', error);
      setError('Failed to load labels');
    }
  }, [token]);

  // Fetch labels when component mounts
  useEffect(() => {
    fetchLabels();
  }, [fetchLabels]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLabelClick = (labelName, event) => {
    // Don't trigger label click if clicking on menu button or dropdown
    if (event.target.closest('.label-menu-btn') || event.target.closest('.label-dropdown')) {
      return;
    }
    navigate(`/search/label%3A${encodeURIComponent(labelName)}`);
  };

  const handleMenuClick = (labelId, event) => {
    event.stopPropagation();
    
    if (showDropdown === labelId) {
      setShowDropdown(null);
    } else {
      // Calculate position for fixed positioning
      const rect = event.target.getBoundingClientRect();
      setDropdownPosition({
        top: rect.bottom + 4,
        left: rect.left - 100 // Position dropdown to the left of the button
      });
      setShowDropdown(labelId);
    }
  };

  const handleDeleteLabel = async (labelId) => {
    try {
      const response = await fetch(`http://localhost:3000/api/labels/${labelId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': token,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete label');
      }

      // Refresh labels list
      fetchLabels();
      setShowDropdown(null);
    } catch (error) {
      console.error('Error deleting label:', error);
      setError('Failed to delete label');
    }
    finally {
      window.location.reload();
    }
  };

  const handleChangeColor = (label) => {
    setShowColorModal(label);
    setShowDropdown(null);
  };

  const handleChangeName = (label) => {
    setShowNameModal(label);
    setNameInput(label.name);
    setShowDropdown(null);
  };

  const handleColorChange = async (labelId, newColor) => {
    try {
      const label = labels.find(l => l.id === labelId);
      const response = await fetch(`http://localhost:3000/api/labels/${labelId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token,
        },
        body: JSON.stringify({ 
          name: label.name,
          color: newColor
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to update label color');
      }

      // Refresh labels list
      fetchLabels();
      setShowColorModal(null);
    } catch (error) {
      console.error('Error updating label color:', error);
      setError('Failed to update label color');
    }
  };

  const handleNameChange = async () => {
    if (!nameInput.trim()) {
      setError('Label name cannot be empty');
      return;
    }

    // Check if name already exists (case insensitive)
    const nameExists = labels.some(
      label => label.id !== showNameModal.id && 
      label.name.toLowerCase() === nameInput.trim().toLowerCase()
    );

    if (nameExists) {
      setError('A label with this name already exists');
      return;
    }

    try {
      const response = await fetch(`http://localhost:3000/api/labels/${showNameModal.id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token,
        },
        body: JSON.stringify({ 
          name: nameInput.trim(),
          color: showNameModal.color
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to update label name');
      }

      // Refresh labels list
      fetchLabels();
      setShowNameModal(null);
      setNameInput('');
      setError('');
    } catch (error) {
      console.error('Error updating label name:', error);
      setError('Failed to update label name');
    }
  };

  const openModal = () => {
    setShowModal(true);
    setError('');
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const closeColorModal = () => {
    setShowColorModal(null);
  };

  const closeNameModal = () => {
    setShowNameModal(null);
    setNameInput('');
    setError('');
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
          {labels.map((label) => {
            const isActive = activeSection && activeSection.startsWith('label:') && 
                           activeSection.split('label:')[1] === label.name;
            return (
              <div 
                key={label.id} 
                className={`side-item label-item ${isActive ? 'active' : ''}`}
                onClick={(e) => handleLabelClick(label.name, e)}
              >
              <div 
                className="label-color-dot"
                style={{ backgroundColor: label.color || '#4F46E5' }}
              />
              {!collapsed && <span className="label-name">{label.name}</span>}
              {!collapsed && (
                <button 
                  className="label-menu-btn"
                  onClick={(e) => handleMenuClick(label.id, e)}
                  title="Label options"
                >
                  <i className="bi bi-three-dots"></i>
                </button>
              )}
              {/* Dropdown menu */}
              {showDropdown === label.id && !collapsed && (
                <div 
                  className="label-dropdown" 
                  ref={dropdownRef}
                  style={{
                    top: `${dropdownPosition.top}px`,
                    left: `${dropdownPosition.left}px`
                  }}
                >
                  <button 
                    className="label-dropdown-item"
                    onClick={() => handleChangeName(label)}
                  >
                    <i className="bi bi-pencil-square"></i>
                    Change Name
                  </button>
                  <button 
                    className="label-dropdown-item"
                    onClick={() => handleChangeColor(label)}
                  >
                    <i className="bi bi-palette"></i>
                    Change Color
                  </button>
                  <button 
                    className="label-dropdown-item delete"
                    onClick={() => handleDeleteLabel(label.id)}
                  >
                    <i className="bi bi-trash"></i>
                    Delete Label
                  </button>
                </div>
              )}
            </div>
            );
          })}
        </div>
      </div>

      {/* Add Label Modal */}
      {showModal && (
        <AddLabelModal
          onClose={closeModal}
          onLabelAdded={handleLabelAdded}
        />
      )}

      {/* Change Name Modal */}
      {showNameModal && (
        <div className="label-modal-overlay">
          <div className="label-modal">
            <div className="label-modal-header">
              <h3>Change Label Name</h3>
              <button 
                className="label-modal-close"
                onClick={closeNameModal}
                title="Close"
              >
                <i className="bi bi-x"></i>
              </button>
            </div>
            
            <div className="label-modal-content">
              <div className="label-modal-field">
                <label>Label Name</label>
                <input
                  type="text"
                  className="label-modal-input"
                  value={nameInput}
                  onChange={(e) => setNameInput(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') {
                      handleNameChange();
                    } else if (e.key === 'Escape') {
                      closeNameModal();
                    }
                  }}
                  placeholder="Enter label name"
                  autoFocus
                />
              </div>
            </div>

            <div className="label-modal-actions">
              <button 
                className="label-modal-btn label-modal-btn-cancel"
                onClick={closeNameModal}
              >
                Cancel
              </button>
              <button 
                className="label-modal-btn label-modal-btn-save"
                onClick={handleNameChange}
                disabled={!nameInput.trim()}
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Change Color Modal */}
      {showColorModal && (
        <div className="label-modal-overlay">
          <div className="label-modal">
            <div className="label-modal-header">
              <h3>Change Label Color</h3>
              <button 
                className="label-modal-close"
                onClick={closeColorModal}
                title="Close"
              >
                <i className="bi bi-x"></i>
              </button>
            </div>
            
            <div className="label-modal-content">
              <div className="label-modal-field">
                <label>Current Label: <strong>{showColorModal.name}</strong></label>
                <div className="color-picker-container">
                  <div className="color-options">
                    {colorOptions.map((color) => (
                      <button
                        key={color}
                        type="button"
                        className={`color-option ${showColorModal.color === color ? 'selected' : ''}`}
                        style={{ backgroundColor: color }}
                        onClick={() => handleColorChange(showColorModal.id, color)}
                        title={`Change to ${color}`}
                      />
                    ))}
                  </div>
                </div>
              </div>
            </div>

            <div className="label-modal-actions">
              <button 
                className="label-modal-btn label-modal-btn-cancel"
                onClick={closeColorModal}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Labels;