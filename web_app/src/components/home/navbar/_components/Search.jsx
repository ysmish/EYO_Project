import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Search = ({user, searchQuery, setSearchQuery}) => {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  // Sync with external search query and auto-search
  useEffect(() => {
    if (searchQuery !== undefined && searchQuery !== null) {
      setQuery(searchQuery);
      if (searchQuery.trim()) {
        // Automatically perform search with the external query
        navigate(`/search/${encodeURIComponent(searchQuery)}`);
        // Don't clear the searchQuery for non-empty queries so it stays visible
      } else {
        // Only clear the external query when it's empty (like from All Mails)
        setSearchQuery('');
      }
    }
  }, [searchQuery, setSearchQuery, navigate]);

  const handleInputChange = (e) => {
    setQuery(e.target.value);
    // Clear any external search query when user starts typing manually
    if (searchQuery) {
      setSearchQuery('');
    }
  };

  const handleSearch = () => {
    if (!query.trim()) return; // Prevent empty searches
    navigate(`/search/${encodeURIComponent(query)}`);
  };

  return (
    <div className='search-bar'>
      <input
        type="text"
        placeholder="Search..."
        className='search-input'
        value={query}
        onChange={handleInputChange}
        onKeyDown={e => { if (e.key === 'Enter') handleSearch(); }}
      />
      <div className="nav-item" onClick={handleSearch}>
        <i className="bi bi-search"></i>
      </div>
    </div>
  );
};

export default Search;