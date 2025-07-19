import mongoose from 'mongoose';

const labelSchema = new mongoose.Schema({
  id: {
    type: Number,
    required: true
  },
  name: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: '#4F46E5'
  },
  username: {
    type: String,
    required: true,
    ref: 'User'
  },
  mails: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Mail'
  }]
}, {
  timestamps: true
});

// Create compound index for username and label id
labelSchema.index({ username: 1, id: 1 }, { unique: true });

const Label = mongoose.model('Label', labelSchema);

export default Label;
