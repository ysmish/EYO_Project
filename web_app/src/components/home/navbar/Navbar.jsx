import Logo from './_components/Logo';
import Search from './_components/Search';
import Settings from './_components/Settings';
import UserButton from './_components/UserButton';

const Navbar = () => {

  return (
    <div className="navbar">
      <Logo />
      <Search />
      <div className='navbar-right'>
        <Settings />
        <UserButton />
      </div>
    </div>
  );
};

export default Navbar;