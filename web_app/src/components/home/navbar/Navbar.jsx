import Logo from './_components/Logo';
import Search from './_components/Search';
import Settings from './_components/Settings';
import UserButton from './_components/UserButton';

const Navbar = ({user, setUser}) => {

  return (
    <div className="navbar">
      <Logo />
      <Search user={user}/>
      <div className='navbar-right'>
        <Settings />
        <UserButton user={user} setUser={setUser}/>
      </div>
    </div>
  );
};

export default Navbar;