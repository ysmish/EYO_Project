import '../../../../styles.css';

const NewMail = ({ onOpenCompose }) => {
  const handleOpenCompose = () => {
    onOpenCompose();
  };

  return (
    <div className='new-mail-button' onClick={handleOpenCompose}>
      <i className="bi bi-pencil-square"></i>
      <span>New Mail</span>
    </div>
  );
};

export default NewMail;