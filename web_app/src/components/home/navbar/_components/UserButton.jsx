import { useRef, useState, useEffect } from 'react';
import '../../../../styles.css';
import { useAuth } from '../../../../context/AuthProvider';

const UserButton = ({ user, setUser}) => {
  const { logOut } = useAuth();
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target)
      ) {
        setOpen(false);
      }
    }
    if (open) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [open, user]);

  // Handle photo change
  const handlePhotoChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = async (ev) => {
      const response = await fetch('http://localhost:3000/api/users/photo', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          photo: ev.target.result,
        }),
      });
      if (response.ok) {
        setUser((prev) => ({ ...prev, photo: ev.target.result }));
      } else {
        console.error('Failed to update photo');
      }
  }
    reader.readAsDataURL(file);
  };

  return (
    <div className="nav-item" style={{ position: 'relative' }}>
      <button
        className="nav-item"
        aria-label="User menu"
        style={{
          border: 'none',
          background: 'none',
          padding: 0,
          outline: 'none',
        }}
        onMouseDown={e => e.stopPropagation()}
        onClick={() => setOpen(v => !v)}
      >
        <img
          src={user?.photo}
          alt="User"
          style={{
            width: 25,
            height: 25,
            borderRadius: '50%',
            objectFit: 'cover',
            border: '1px solid var(--shadow-color)',
          }}
        />
      </button>
      {open && (
        <div
          ref={dropdownRef}
          tabIndex={-1}
          className="auth-card"
          style={{
            position: 'absolute',
            right: 0,
            top: 40,
            minWidth: 260,
            zIndex: 100,
            boxShadow: '0 4px 16px rgba(0,0,0,0.08)',
            padding: '1.5rem 1.5rem 1rem 1.5rem',
          }}
        >
          <button
            className="nav-item"
            style={{
              position: 'absolute',
              top: 10,
              right: 10,
              background: 'none',
              border: 'none',
              fontSize: 18,
              cursor: 'pointer',
              color: 'var(--text-color)',
            }}
            aria-label="Close"
            onClick={() => setOpen(false)}
          >
            ×
          </button>
          <div style={{ textAlign: 'center', marginBottom: 16 }}>
            <label htmlFor="user-photo-upload" style={{ cursor: 'pointer' }}>
              <img
                src={user.photo || '/default-avatar.png'}
                alt="User"
                className="preview-image"
                style={{
                  width: 72,
                  height: 72,
                  borderRadius: '50%',
                  objectFit: 'cover',
                  border: '2px solid var(--shadow-color)',
                  marginBottom: 8,
                }}
              />
              <input
                id="user-photo-upload"
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                onChange={handlePhotoChange}
              />
            </label>
            <div style={{ fontWeight: 600, marginTop: 8 }}>
              {user.firstName} {user.lastName}
            </div>
            <div style={{ fontSize: 13, color: 'var(--text-color)', opacity: 0.7 }}>
              {user.email}
            </div>
          </div>
          <button
            className="auth-button primary"
            style={{ width: '100%', marginTop: 12 }}
            onClick={logOut}
          >
            Logout
          </button>
        </div>
      )}
    </div>
  );
};

export default UserButton;