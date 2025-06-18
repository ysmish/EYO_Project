import { useState } from 'react';
import '../../../../styles.css';
import ComposeModal from './ComposeModal';

const NewMail = () => {
  const [isComposeOpen, setIsComposeOpen] = useState(false);

  const handleOpenCompose = () => {
    setIsComposeOpen(true);
  };

  const handleCloseCompose = () => {
    setIsComposeOpen(false);
  };

  return (
    <>
      <div className='side-item' onClick={handleOpenCompose}>
        <i className="bi bi-pencil-square"></i>
        <span>New Mail</span>
      </div>
      {isComposeOpen && (
        <ComposeModal onClose={handleCloseCompose} />
      )}
    </>
  );
};

export default NewMail;