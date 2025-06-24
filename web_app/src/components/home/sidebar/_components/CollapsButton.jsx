import '../../../../styles.css';
import "bootstrap-icons/font/bootstrap-icons.css";

const CollapseButton = ({ collapsed, setCollapsed }) => {
  return (
    <div className="side-item" onClick={setCollapsed}>
      <i className={`bi ${collapsed ? 'bi-chevron-double-right' : 'bi-chevron-double-left'}`}></i>
      <span>{collapsed ? 'Expand' : 'Collapse'}</span>
    </div>
  );
};

export default CollapseButton;