import { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Search = ({searchQuery, setSearchQuery}) => {
  const [query, setQuery] = useState('');
  const [isUserTyping, setIsUserTyping] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const previousPath = useRef(location.pathname);

  // Helper function to add trailing space for display
  const formatQueryForDisplay = (queryStr) => {
    // Add trailing space for common search patterns if not already present
    if (queryStr && !queryStr.endsWith(' ') && 
        (queryStr.startsWith('in:') || queryStr.startsWith('label:'))) {
      return queryStr + ' ';
    }
    return queryStr;
  };

  // Sync search bar with URL when navigating via sidebar or other means
  useEffect(() => {
    const path = location.pathname;
    
    // Only sync if the path actually changed (not just from typing)
    if (previousPath.current !== path && !isUserTyping) {
      previousPath.current = path;
      
      if (path.startsWith('/search/')) {
        const urlQuery = path.split('/search/')[1];
        if (urlQuery && !urlQuery.includes('/')) { // Make sure it's not a mail detail URL
          // Safely decode the URL query with error handling
          let decodedQuery;
          try {
            decodedQuery = decodeURIComponent(urlQuery);
          } catch (error) {
            console.warn('Failed to decode URL parameter:', urlQuery, error);
            // Use the raw parameter if decoding fails
            decodedQuery = urlQuery;
          }
          const displayQuery = formatQueryForDisplay(decodedQuery);
          setQuery(displayQuery);
          // Update the external search query to keep things in sync
          if (setSearchQuery) {
            setSearchQuery(displayQuery);
          }
        }
      }
    }
  }, [location.pathname, setSearchQuery, navigate, isUserTyping]);

  // Handle external search query changes (for manual updates, not sidebar navigation)
  useEffect(() => {
    if (searchQuery !== undefined && searchQuery !== null && !isUserTyping) {
      if (searchQuery === '') {
        setQuery('');
      } else if (searchQuery !== query) {
        // Only update if it's different to avoid infinite loops
        setQuery(searchQuery);
      }
    }
  }, [searchQuery, query, isUserTyping]);

  const handleInputChange = (e) => {
    setIsUserTyping(true);
    setQuery(e.target.value);
  };

  const handleSearch = () => {
    if (!query.trim()) return; // Prevent empty searches
    setIsUserTyping(false); // Reset typing flag
    // Remove trailing space before encoding for URL
    const trimmedQuery = query.trim();
    navigate(`/search/${encodeURIComponent(trimmedQuery)}`);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  // Reset typing flag when user clicks outside or stops interacting
  const handleBlur = () => {
    // Small delay to allow for search button clicks
    setTimeout(() => {
      setIsUserTyping(false);
    }, 100);
  };

  return (
    <div className='search-bar'>
      <input
        type="text"
        placeholder="Search..."
        className='search-input'
        value={query}
        onChange={handleInputChange}
        onKeyDown={handleKeyDown}
        onBlur={handleBlur}
      />
      <div className="nav-item" onClick={handleSearch}>
        <i className="bi bi-search"></i>
      </div>
    </div>
  );
};

export default Search;