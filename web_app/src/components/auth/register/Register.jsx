import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormValidation } from './hooks/useFormValidation';
import { useImageUpload } from './hooks/useImageUpload';
import FormField from './components/FormField';
import ImageUploadField from './components/ImageUploadField';
import Settings from '../../home/navbar/_components/Settings';
import { useAuth } from '../../../context/AuthProvider';
import { useEffect } from 'react';
import '../../../styles.css';

const Register = () => {
  const navigate = useNavigate();
  const { token } = useAuth();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    birthday: '',
    username: '',
    password: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  
  const { errors, validateForm, clearError, setGeneralError, clearAllErrors } = useFormValidation();
  const { imagePreview, imageData, handleImageUpload, removeImage } = useImageUpload();

  // Redirect if already logged in
  useEffect(() => {
    if (token) {
      navigate('/');
    }
  }, [token, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    clearError(name);
  };

  const handleImageChange = (file) => {
    handleImageUpload(file, (error) => {
      setGeneralError(error);
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm(formData)) return;

    setLoading(true);
    clearAllErrors();

    try {
      const registrationData = {
        ...formData,
        photo: imageData || null
      };

      const response = await fetch('http://localhost:3000/api/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registrationData),
      });

      const data = await response.json();

      if (response.ok) {
        alert('Registration successful! Please login with your credentials.');
        navigate('/login');
      } else {
        setGeneralError(data.error || data.message || 'Registration failed');
      }
    } catch (error) {
      console.error('Registration error:', error);
      setGeneralError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-card-header">
          <Settings />
          <div className="auth-header">
            <h1>Create Account</h1>
            <p>Join our mail system</p>
          </div>
        </div>
        

        <form onSubmit={handleSubmit} className="auth-form">
          {errors.general && (
            <div className="error-message general-error">{errors.general}</div>
          )}


          <div className="form-row">
            <FormField
              id="firstName"
              name="firstName"
              label="First Name"
              value={formData.firstName}
              onChange={handleChange}
              error={errors.firstName}
              placeholder="Enter your first name"
              required
            />
            <FormField
              id="lastName"
              name="lastName"
              label="Last Name"
              value={formData.lastName}
              onChange={handleChange}
              error={errors.lastName}
              placeholder="Enter your last name"
              required
            />
          </div>

          <FormField
            id="username"
            name="username"
            label="Username"
            value={formData.username}
            onChange={handleChange}
            error={errors.username}
            placeholder="Choose a username"
            required
          />

          <FormField
            id="birthday"
            name="birthday"
            type="date"
            label="Birthday"
            value={formData.birthday}
            onChange={handleChange}
            error={errors.birthday}
            required
          />

          <div className="form-row">
            <FormField
              id="password"
              name="password"
              type="password"
              label="Password"
              value={formData.password}
              onChange={handleChange}
              error={errors.password}
              placeholder="Create a password"
              required
            />
            <FormField
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              label="Confirm Password"
              value={formData.confirmPassword}
              onChange={handleChange}
              error={errors.confirmPassword}
              placeholder="Confirm your password"
              required
            />
          </div>

          <ImageUploadField
            imagePreview={imagePreview}
            onImageUpload={handleImageChange}
            onRemoveImage={removeImage}
            error={errors.photo}
          />

          <button 
            type="submit" 
            className="auth-button primary"
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account?{' '}
            <button type="button" className="link-button" onClick={() => navigate('/login')}>
              Sign in
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register; 