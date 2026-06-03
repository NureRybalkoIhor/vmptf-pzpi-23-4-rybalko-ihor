import React, { useState } from 'react';

function JsonTreeNode({ data, label, isLast = true }) {
  const [collapsed, setCollapsed] = useState(false);

  const type = typeof data;
  const isObject = data !== null && type === 'object';

  const toggleCollapse = () => setCollapsed(!collapsed);

  if (isObject) {
    const keys = Object.keys(data);
    const isArray = Array.isArray(data);
    const opener = isArray ? '[' : '{';
    const closer = isArray ? ']' : '}';

    return (
      <div className="json-tree-node">
        <div className="json-tree-header" onClick={toggleCollapse}>
          {keys.length > 0 && (
            <span className={`json-tree-arrow ${collapsed ? '' : 'expanded'}`}>▶</span>
          )}
          {label && <span className="json-tree-key">"{label}": </span>}
          <span className="json-tree-bracket">{opener}</span>
          {collapsed && <span className="json-tree-collapsed-text"> ... {keys.length} items </span>}
          {collapsed && <span className="json-tree-bracket">{closer}{!isLast && ','}</span>}
        </div>

        {!collapsed && (
          <div className="json-tree-children">
            {keys.map((key, index) => (
              <JsonTreeNode
                key={key}
                label={isArray ? null : key}
                data={data[key]}
                isLast={index === keys.length - 1}
              />
            ))}
          </div>
        )}

        {!collapsed && (
          <div className="json-tree-footer">
            <span className="json-tree-bracket">{closer}{!isLast && ','}</span>
          </div>
        )}
      </div>
    );
  }


  let valueStr = '';
  let valClass = '';

  if (data === null) {
    valueStr = 'null';
    valClass = 'json-val-null';
  } else if (type === 'string') {
    valueStr = `"${data}"`;
    valClass = 'json-val-string';
  } else if (type === 'number') {
    valueStr = String(data);
    valClass = 'json-val-number';
  } else if (type === 'boolean') {
    valueStr = data ? 'true' : 'false';
    valClass = 'json-val-boolean';
  } else {
    valueStr = String(data);
    valClass = 'json-val-other';
  }

  return (
    <div className="json-tree-node primitive">
      {label && <span className="json-tree-key">"{label}": </span>}
      <span className={`json-tree-value ${valClass}`}>{valueStr}</span>
      {!isLast && <span className="json-tree-comma">,</span>}
    </div>
  );
}

export default function JsonParser() {
  const [jsonInput, setJsonInput] = useState('{\n  "projectName": "Практична 2",\n  "status": "В процесі",\n  "rating": 5,\n  "details": {\n    "framework": "React + Vite",\n    "styles": "React CSS",\n    "completedLevels": [1, 2]\n  },\n  "active": true\n}');
  const [parsedData, setParsedData] = useState(null);
  const [error, setError] = useState(null);

  const handleParse = () => {
    try {
      const parsed = JSON.parse(jsonInput);
      setParsedData(parsed);
      setError(null);
    } catch (err) {
      setError(err.message);
      setParsedData(null);
    }
  };

  React.useEffect(() => {
    handleParse();
  }, []);

  return (
    <div className="json-parser-wrapper fade-in">
      <div className="parser-container">
        <div className="parser-section input-section">
          <h3>Введіть JSON рядок</h3>
          <textarea
            className="json-textarea"
            value={jsonInput}
            onChange={(e) => setJsonInput(e.target.value)}
            placeholder="Вставте JSON сюди..."
          />
          <button className="btn btn-primary" onClick={handleParse}>
            Розпарсити JSON
          </button>
          {error && (
            <div className="error-badge">
              <strong>Помилка валідації:</strong> {error}
            </div>
          )}
        </div>

        <div className="parser-section output-section">
          <h3>Деревоподібне відображення</h3>
          <div className="tree-viewer-box">
            {parsedData !== null ? (
              <div className="json-tree-root">
                <JsonTreeNode data={parsedData} isLast={true} />
              </div>
            ) : (
              <div className="tree-empty-state">
                Немає розпарсених даних. Будь ласка, введіть валідний JSON.
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
