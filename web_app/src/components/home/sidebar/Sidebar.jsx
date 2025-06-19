import Inbox from './_components/Inbox';
import Starred from './_components/Starred';
import Sent from './_components/Sent';
import AllMails from './_components/AllMails';
import Labels from './_components/Label';
import NewMail from './_components/NewMail';

const Sidebar = ({setSearchQuery}) => {

  return (
    <div className='sidebar'>
        <NewMail />
        <Inbox setSearchQuery={setSearchQuery} />
        <Starred setSearchQuery={setSearchQuery} />
        <Sent setSearchQuery={setSearchQuery} />
        <AllMails setSearchQuery={setSearchQuery} />
        <Labels setSearchQuery={setSearchQuery} />
    </div>
  );
};

export default Sidebar;