import mongoose from 'mongoose';

const mailSchema = new mongoose.Schema({
  mailId: {
    type: Number,
    required: true
  },
  from: {
    type: String,
    required: true,
    ref: 'User'
  },
  to: [{
    type: String,
    required: true,
    ref: 'User'
  }],
  cc: [{
    type: String,
    ref: 'User'
  }],
  subject: {
    type: String,
    required: true
  },
  body: {
    type: String,
    required: true
  },
  date: {
    type: Date,
    default: Date.now
  },
  read: {
    type: Boolean,
    default: false
  },
  attachments: [{
    filename: String,
    mimetype: String,
    size: Number,
    data: Buffer
  }],
  labels: [{
    type: Number,
  }],
  owner: {
    type: String,
    required: true,
    ref: 'User'
  }
}, {
  timestamps: true
});

// Create compound index for owner and mailId
mailSchema.index({ owner: 1, mailId: 1 }, { unique: true });

const Mail = mongoose.model('Mail', mailSchema);

export default Mail;
