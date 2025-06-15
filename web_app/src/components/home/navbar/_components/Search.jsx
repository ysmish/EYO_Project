import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const Search = () => {
  return (
    <div className='search-bar'>
      <input type="text" placeholder="Search..." className='search-input' />
      <div className="nav-item" onClick={() => console.log('Search clicked')}>
        <i className="bi bi-search"></i>
      </div>
      
    </div>
  );
};

export default Search;