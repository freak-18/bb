import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getBookingById, processBookingPayment } from '../utils/api';
import { eventBus, EVENTS } from '../utils/eventBus';

const PaymentPage = () => {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const [booking, setBooking] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isProcessing, setIsProcessing] = useState(false);
    const [message, setMessage] = useState('');

    // Dynamically load the Google Pay script
    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://pay.google.com/gp/p/js/pay.js';
        script.async = true;
        script.onload = () => {
            console.log('Google Pay script loaded successfully');
        };
        script.onerror = () => {
            console.error('Failed to load Google Pay script');
            setMessage('Could not load Google Pay. Please check your network or try test payment.');
        };
        document.body.appendChild(script);

        return () => {
            if (document.body.contains(script)) {
                document.body.removeChild(script);
            }
        };
    }, []);
    useEffect(() => {
        const fetchBooking = async () => {
            try {
                const response = await getBookingById(bookingId);
                if (response.data.status !== 'PENDING') {
                    setMessage(`This booking has already been processed (Status: ${response.data.status}).`);
                    setBooking(null); // Prevent payment
                } else {
                    setBooking(response.data);
                }
            } catch (error) {
                console.error("Failed to fetch booking details:", error);
                
                // Fallback: Try to get booking from localStorage
                const localBookings = JSON.parse(localStorage.getItem('hotelBookings') || '[]');
                const localBooking = localBookings.find(b => b.bookingId === parseInt(bookingId));
                
                if (localBooking && localBooking.status === 'PENDING') {
                    setBooking(localBooking);
                } else if (localBooking) {
                    setMessage(`This booking has already been processed (Status: ${localBooking.status}).`);
                    setBooking(null);
                } else {
                    setMessage('Booking not found.');
                }
            } finally {
                setIsLoading(false);
            }
        };
        fetchBooking();
    }, [bookingId]);

    const handleTestPayment = async () => {
        setIsProcessing(true);
        setMessage('Processing test payment...');
        
        // Simulate payment processing delay
        setTimeout(async () => {
            try {
                // Try to update via backend
                await processBookingPayment(bookingId);
            } catch (error) {
                console.log('Backend payment processing failed, using fallback');
            }
            
            // Update local storage
            const allBookings = JSON.parse(localStorage.getItem('hotelBookings') || '[]');
            const updatedBookings = allBookings.map(b => 
                b.bookingId === parseInt(bookingId) ? { ...b, status: 'APPROVED' } : b
            );
            localStorage.setItem('hotelBookings', JSON.stringify(updatedBookings));

            eventBus.emit(EVENTS.BOOKING_UPDATED, { bookingId, status: 'APPROVED' });
            eventBus.emit(EVENTS.DATA_REFRESH, { source: 'payment_success' });

            setMessage('Payment successful! Your booking is confirmed.');
            setTimeout(() => navigate('/bookings'), 2000);
            setIsProcessing(false);
        }, 2000);
    };

    const displayGooglePay = async () => {
        setIsProcessing(true);
        setMessage('');

        if (!window.google || !window.google.payments) {
            setMessage('Google Pay is not available. Please try test payment.');
            setIsProcessing(false);
            return;
        }

        try {
            const paymentsClient = new window.google.payments.api.PaymentsClient({
                environment: process.env.REACT_APP_GOOGLE_PAY_ENVIRONMENT || 'TEST'
            });

            const paymentDataRequest = {
                apiVersion: 2,
                apiVersionMinor: 0,
                allowedPaymentMethods: [{
                    type: 'CARD',
                    parameters: {
                        allowedAuthMethods: ['PAN_ONLY', 'CRYPTOGRAM_3DS'],
                        allowedCardNetworks: ['MASTERCARD', 'VISA']
                    },
                    tokenizationSpecification: {
                        type: 'PAYMENT_GATEWAY',
                        parameters: {
                            gateway: process.env.REACT_APP_GOOGLE_PAY_GATEWAY || 'example',
                            gatewayMerchantId: process.env.REACT_APP_GOOGLE_PAY_GATEWAY_MERCHANT_ID || 'exampleGatewayMerchantId'
                        }
                    }
                }],
                merchantInfo: {
                    merchantId: process.env.REACT_APP_GOOGLE_PAY_MERCHANT_ID || 'BCR2DN4T2ZFQZQZQ',
                    merchantName: 'ZENStay Hotel Booking'
                },
                transactionInfo: {
                    totalPriceStatus: 'FINAL',
                    totalPrice: booking.totalPrice.toString(),
                    currencyCode: 'INR',
                    displayItems: [{
                        label: `Booking #${booking.bookingId}`,
                        type: 'LINE_ITEM',
                        price: booking.totalPrice.toString()
                    }]
                }
            };

            const paymentData = await paymentsClient.loadPaymentData(paymentDataRequest);
            
            // Payment successful
            try {
                await processBookingPayment(bookingId);
            } catch (error) {
                console.log('Backend payment processing failed, using fallback');
            }
            
            // Update local storage
            const allBookings = JSON.parse(localStorage.getItem('hotelBookings') || '[]');
            const updatedBookings = allBookings.map(b => 
                b.bookingId === parseInt(bookingId) ? { ...b, status: 'APPROVED' } : b
            );
            localStorage.setItem('hotelBookings', JSON.stringify(updatedBookings));

            eventBus.emit(EVENTS.BOOKING_UPDATED, { bookingId, status: 'APPROVED' });
            eventBus.emit(EVENTS.DATA_REFRESH, { source: 'payment_success' });

            setMessage('Payment successful! Your booking is confirmed.');
            setTimeout(() => navigate('/bookings'), 2000);
            
        } catch (error) {
            console.error('Google Pay error:', error);
            if (error.statusCode === 'CANCELED') {
                setMessage('Payment was cancelled.');
            } else {
                setMessage('Payment failed. Please try test payment.');
            }
        } finally {
            setIsProcessing(false);
        }
    };

    if (isLoading) {
        return <div className="container py-5 text-center"><div className="spinner-border text-primary"></div></div>;
    }

    if (!booking) {
        return <div className="container py-5 text-center"><div className="alert alert-warning">{message || 'Could not load booking details.'}</div></div>;
    }

    return (
        <div className="container py-5">
            <div className="row justify-content-center">
                <div className="col-lg-6">
                    <div className="card shadow-lg border-0">
                        <div className="card-header bg-primary text-white text-center">
                            <h4 className="mb-0"><i className="fas fa-credit-card me-2"></i>Secure Payment</h4>
                        </div>
                        <div className="card-body p-4">
                            <h5 className="card-title text-center mb-4">Booking Summary</h5>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Booking ID:</span>
                                <span className="fw-bold">#{booking.bookingId}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Guest:</span>
                                <span className="fw-bold">{booking.guestName}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-2">
                                <span>Room:</span>
                                <span className="fw-bold">{booking.room.roomType || 'N/A'}</span>
                            </div>
                            <div className="d-flex justify-content-between mb-3">
                                <span>Total Nights:</span>
                                <span className="fw-bold">
                                    {Math.ceil(Math.abs(new Date(booking.checkOutDate) - new Date(booking.checkInDate)) / (1000 * 60 * 60 * 24))}
                                </span>
                            </div>
                            <hr />
                            <div className="d-flex justify-content-between align-items-center mb-4">
                                <span className="h5 mb-0">Total Amount:</span>
                                <span className="h4 mb-0 text-primary fw-bold">₹{booking.totalPrice.toLocaleString() || '0'}</span>
                            </div>
                            
                            {message && (
                                <div className={`alert ${message.includes('failed') ? 'alert-danger' : 'alert-success'}`}>
                                    {message}
                                </div>
                            )}

                            <div className="d-grid gap-2">
                                <button 
                                    className="btn btn-success btn-lg fw-semibold" 
                                    onClick={displayGooglePay} 
                                    disabled={isProcessing || !booking}
                                    style={{backgroundColor: '#4285f4', borderColor: '#4285f4'}}
                                >
                                    {isProcessing ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                                            Processing...
                                        </>
                                    ) : (
                                        <><i className="fab fa-google-pay me-2"></i>Pay with Google Pay</>
                                    )}
                                </button>
                                
                                <button className="btn btn-primary btn-lg fw-semibold" onClick={handleTestPayment} disabled={isProcessing || !booking}>
                                    {isProcessing ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                                            Processing...
                                        </>
                                    ) : (
                                        <><i className="fas fa-credit-card me-2"></i>Test Payment (Demo)</>
                                    )}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PaymentPage;