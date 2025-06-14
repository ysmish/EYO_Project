import Inbox from './_components/Inbox';
import Label from './_components/Label';
import NewMail from './_components/NewMail';

const Sidebar = () => {

  return (
    <div className='sidebar'>
        <NewMail />
        <Inbox />
        <Label />
    </div>
  );
};

export default Sidebar;