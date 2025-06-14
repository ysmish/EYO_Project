import Image from 'react-bootstrap/Image';
import Button from 'react-bootstrap/Button';
import '../../../../styles.css';
import { useTheme } from '../../../../context/ThemeProvider'; // Assuming you have a ThemeProvider context

const Logo = () => {
  const { darkMode } = useTheme(); // Use the theme context to get the current theme
  return (
    <Button
      variant="link"
      href="/"
      className='logo'
    >
      <Image
        src={darkMode ? 'logo_w.svg' : 'logo_b.svg'}
        alt="Logo"
        width={40}
        height={40}
        roundedCircle
      />
    </Button>
  );
};

export default Logo;