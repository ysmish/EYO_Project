import Inbox from './_components/Inbox';
import Sent from './_components/Sent';
import AllMails from './_components/AllMails';
import Label from './_components/Label';
import NewMail from './_components/NewMail';

const Sidebar = ({setSearchQuery}) => {

  return (
    <div className='sidebar'>
        <NewMail />
        <Inbox setSearchQuery={setSearchQuery} />
        <Sent setSearchQuery={setSearchQuery} />
        <AllMails setSearchQuery={setSearchQuery} />
        <Label />
    </div>
  );
};

export default Sidebar;