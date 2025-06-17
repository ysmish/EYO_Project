import { useState } from 'react';

export const useImageUpload = () => {
  const [imagePreview, setImagePreview] = useState(null);
  const [imageData, setImageData] = useState('');

  const handleImageUpload = (file, onError) => {
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      onError('Please select a valid image file');
      return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      onError('Image size must be less than 5MB');
      return;
    }

    // Convert to base64
    const reader = new FileReader();
    reader.onload = (e) => {
      const base64String = e.target.result;
      setImageData(base64String);
      setImagePreview(base64String);
    };
    reader.readAsDataURL(file);
  };

  const removeImage = () => {
    setImageData('');
    setImagePreview(null);
    // Clear the file input
    const fileInput = document.getElementById('photo');
    if (fileInput) {
      fileInput.value = '';
    }
  };

  const resetImage = () => {
    setImageData('');
    setImagePreview(null);
  };

  return {
    imagePreview,
    imageData,
    handleImageUpload,
    removeImage,
    resetImage
  };
}; 