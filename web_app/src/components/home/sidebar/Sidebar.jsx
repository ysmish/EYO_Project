import { useLocation } from 'react-router-dom';
import Inbox from './_components/Inbox';
import Starred from './_components/Starred';
import Sent from './_components/Sent';
import Drafts from './_components/Drafts';
import AllMails from './_components/AllMails';
import Labels from './_components/Label';
import NewMail from './_components/NewMail';

const Sidebar = ({ onOpenCompose }) => {
  const location = useLocation();
  
  // Helper function to determine active section from URL
  const getActiveSection = () => {
    const path = location.pathname;
    if (path.startsWith('/search/')) {
      const searchQuery = path.split('/search/')[1];
      if (searchQuery) {
        try {
          const decodedQuery = decodeURIComponent(searchQuery.split('/')[0]).trim(); // Get query before mail ID if present
          // Use exact matching instead of includes
          if (decodedQuery === 'in:inbox') return 'inbox';
          if (decodedQuery === 'in:starred') return 'starred';
          if (decodedQuery === 'in:sent') return 'sent';
          if (decodedQuery === 'in:drafts') return 'drafts';
          if (decodedQuery === 'in:all') return 'allmails';
          if (decodedQuery.startsWith('label:') && decodedQuery.split(':').length === 2) {
            return `label:${decodedQuery.split('label:')[1]}`;
          }
        } catch (e) {
          console.warn('Failed to decode search query:', searchQuery);
        }
      }
    }
    return null;
  };

  const activeSection = getActiveSection();

  return (
    <div className='sidebar'>
        <NewMail onOpenCompose={onOpenCompose} />
        <Inbox isActive={activeSection === 'inbox'} />
        <Starred isActive={activeSection === 'starred'} />
        <Sent isActive={activeSection === 'sent'} />
        <Drafts isActive={activeSection === 'drafts'} />
        <AllMails isActive={activeSection === 'allmails'} />
        <Labels activeSection={activeSection} />
    </div>
  );
};

export default Sidebar;