import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import { useTheme } from '../../../../context/ThemeProvider';

const Settings = () => {
  const { toggleTheme, darkMode } = useTheme();

  return (
    <div onClick={toggleTheme} className='nav-item'>
      <i className={`h1bi ${darkMode ? 'bi-sun' : 'bi-moon'}`}></i>
    </div>
  );
};

export default Settings;