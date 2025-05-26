import express from 'express';
import apiRouter from './routes/api.js';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use('/api', apiRouter);

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});