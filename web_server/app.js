import express from 'express';
import apiRouter from './routes/api.js';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
// Error handler for invalid JSON
app.use((err, req, res, next) => {
  if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
    return res.status(400).json({ error: 'Invalid JSON.' });
  }
  next(err);
});


app.use('/api', apiRouter);



app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});