import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL ? `${process.env.REACT_APP_API_URL}/api` : 'http://localhost:8080/api';

export const getBookings = () => axios.get(`${API_URL}/bookings`);
export const createBooking = (data) => axios.post(`${API_URL}/bookings`, data);
export const getBookingById = (bookingId) => axios.get(`${API_URL}/bookings/${bookingId}`);
export const processBookingPayment = (bookingId) => axios.put(`${API_URL}/bookings/${bookingId}/payment`);
export const updateBookingStatus = (bookingId, status) =>
  axios.put(`${API_URL}/bookings/${bookingId}/status`, { status });
export const cancelBooking = (bookingId) =>
  axios.delete(`${API_URL}/bookings/${bookingId}`);
export const getRooms = (availableOnly = false) =>
  axios.get(`${API_URL}/rooms${availableOnly ? '?available=true' : ''}`);
export const freeRoom = (roomId) =>
  axios.put(`${API_URL}/rooms/${roomId}/free`);
export const freeAllRooms = () =>
  axios.put(`${API_URL}/rooms/free-all`);
