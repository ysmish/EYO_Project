import { useRef, useState, useEffect } from 'react';
import '../../../../styles.css';
import { useAuth } from '../../../../context/AuthProvider';

const UserButton = ({ user, setUser }) => {
  const { logOut } = useAuth();
  const [open, setOpen] = useState(false);
  const [showCamera, setShowCamera] = useState(false);
  const [stream, setStream] = useState(null);
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
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

  // Start camera stream
  const openCamera = async () => {
    try {
      const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
      setStream(mediaStream);
      setShowCamera(true);
    } catch (err) {
      alert('Could not access camera.');
    }
  };

  // Attach stream to video element
  useEffect(() => {
    if (showCamera && videoRef.current && stream) {
      videoRef.current.srcObject = stream;
    }
    return () => {
      if (stream) {
        stream.getTracks().forEach(track => track.stop());
      }
    };
  }, [showCamera, stream]);

  // Capture photo from camera
  const handleCapture = async () => {
    const video = videoRef.current;
    const canvas = canvasRef.current;
    if (!video || !canvas) return;
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    canvas.toBlob(async (blob) => {
      if (!blob) return;
      const formData = new FormData();
      formData.append('photo', blob, 'photo.jpg');
      const response = await fetch('http://localhost:3000/api/users/photo', {
        method: 'POST',
        headers: {
          Authorization: `${localStorage.getItem('token')}`,
        },
        body: formData,
      });
      if (response.ok) {
        const reader = new FileReader();
        reader.onload = (ev) => {
          setUser((prev) => ({ ...prev, photo: ev.target.result }));
        };
        reader.readAsDataURL(blob);
      } else {
        alert('Failed to upload photo');
      }
      setShowCamera(false);
      setStream(null);
    }, 'image/jpeg');
  };

  // Handle photo change (file upload)
  const handlePhotoChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('photo', file);

    const response = await fetch('http://localhost:3000/api/users/photo', {
      method: 'POST',
      headers: {
        Authorization: `${localStorage.getItem('token')}`,
      },
      body: formData,
    });

    if (response.ok) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        setUser((prev) => ({ ...prev, photo: ev.target.result }));
      };
      reader.readAsDataURL(file);
    } else {
      console.error('Failed to update photo');
    }
  };

  // Handle photo delete
  const handleDeletePhoto = async () => {
    const formData = new FormData();
    formData.append('photo', '');

    const response = await fetch('http://localhost:3000/api/users/photo', {
      method: 'POST',
      headers: {
        Authorization: `${localStorage.getItem('token')}`,
      },
      body: formData,
    });

    if (response.ok) {
      window.location.reload();
    } else {
      console.error('Failed to delete photo');
    }
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
                capture="user"
                style={{ display: 'none' }}
                onChange={handlePhotoChange}
              />
            </label>
            <div>
              <button
                type="button"
                style={{
                  marginTop: 8,
                  background: 'none',
                  border: 'none',
                  color: 'var(--danger-color, #e74c3c)',
                  cursor: 'pointer',
                  fontSize: 13,
                  textDecoration: 'underline',
                }}
                onClick={handleDeletePhoto}
              >
                Delete Photo
              </button>
            </div>
            <div>
              <button
                type="button"
                style={{
                  marginTop: 8,
                  background: 'none',
                  border: '1px solid var(--primary-color, #3498db)',
                  color: 'var(--primary-color, #3498db)',
                  cursor: 'pointer',
                  fontSize: 13,
                  borderRadius: 4,
                  padding: '4px 10px',
                  marginBottom: 4,
                }}
                onClick={openCamera}
              >
                Take Photo
              </button>
            </div>
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
      {showCamera && (
        <div
          style={{
            position: 'fixed',
            top: 0, left: 0, right: 0, bottom: 0,
            background: 'rgba(0,0,0,0.7)',
            zIndex: 9999,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <div style={{
            background: '#fff',
            padding: 20,
            borderRadius: 8,
            textAlign: 'center',
            position: 'relative',
          }}>
            <video
              ref={videoRef}
              autoPlay
              style={{ width: 320, height: 240, borderRadius: 8, background: '#000' }}
            />
            <canvas ref={canvasRef} style={{ display: 'none' }} />
            <div>
              <button
                onClick={handleCapture}
                style={{
                  margin: '12px 8px 0 0',
                  background: 'var(--primary-color, #3498db)',
                  color: '#fff',
                  border: 'none',
                  borderRadius: 4,
                  padding: '8px 16px',
                  cursor: 'pointer',
                }}
              >
                Capture
              </button>
              <button
                onClick={() => {
                  setShowCamera(false);
                  setStream(null);
                }}
                style={{
                  margin: '12px 0 0 0',
                  background: 'none',
                  color: 'var(--danger-color, #e74c3c)',
                  border: 'none',
                  borderRadius: 4,
                  padding: '8px 16px',
                  cursor: 'pointer',
                  textDecoration: 'underline',
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserButton;