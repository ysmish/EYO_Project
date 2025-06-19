import React from 'react';
import "../../../styles.css";

// actions: array of { key, iconClass, label, onClick }
const ActionToolbar = ({ actions = [] }) => {
  return (
    <div className="action-toolbar">
      {actions.map(action => (
        <button
          key={action.key}
          className="action-toolbar-btn"
          title={action.label}
          onClick={action.onClick}
        >
          <i className={action.iconClass}></i>
        </button>
      ))}
    </div>
  );
};

export default ActionToolbar; 