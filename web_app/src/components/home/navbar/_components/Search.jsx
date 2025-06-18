import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Search = ({user}) => {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

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
        onChange={e => setQuery(e.target.value)}
        onKeyDown={e => { if (e.key === 'Enter') handleSearch(); }}
      />
      <div className="nav-item" onClick={handleSearch}>
        <i className="bi bi-search"></i>
      </div>
    </div>
  );
};

export default Search;