import React from 'react';

const ImageUploadField = ({ 
  imagePreview, 
  onImageUpload, 
  onRemoveImage, 
  error 
}) => {
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    onImageUpload(file);
  };

  return (
    <div className="form-group">
      <label htmlFor="photo">Profile Photo (Optional)</label>
      <input
        type="file"
        id="photo"
        name="photo"
        accept="image/*"
        onChange={handleFileChange}
        className={error ? 'error' : ''}
      />
      {error && <span className="error-message">{error}</span>}
      
      {imagePreview && (
        <div className="image-preview">
          <img 
            src={imagePreview} 
            alt="Preview" 
            className="preview-image"
          />
          <button 
            type="button" 
            className="remove-image-btn"
            onClick={onRemoveImage}
          >
            Remove Image
          </button>
        </div>
      )}
    </div>
  );
};

export default ImageUploadField; 