# Google Pay Credentials Guide

## Step 1: Get Google Pay Merchant ID

1. **Visit**: https://pay.google.com/business/console
2. **Sign in** with your Google account
3. **Create Business Profile**:
   - Business name: "ZENStay Hotel Booking"
   - Business type: "Hospitality"
   - Country: India
4. **Get Merchant ID**: Copy the 12-16 character ID (e.g., `BCR2DN4T2ZFQZQZQ`)

## Step 2: Choose Payment Processor

### Option A: Stripe (Recommended)
1. **Visit**: https://stripe.com/in
2. **Create account** with business details
3. **Get API Keys**:
   - Test: `pk_test_51ABC...` (Publishable Key)
   - Live: `pk_live_51ABC...` (Publishable Key)

### Option B: Razorpay (India-focused)
1. **Visit**: https://razorpay.com/
2. **Create merchant account**
3. **Get API Keys**:
   - Test: `rzp_test_ABC123...`
   - Live: `rzp_live_ABC123...`

## Step 3: Update Your Code

### Frontend (.env):
```env
# Google Pay Details
REACT_APP_GOOGLE_PAY_MERCHANT_ID=BCR2DN4T2ZFQZQZQ

# For Stripe:
REACT_APP_GOOGLE_PAY_GATEWAY=stripe
REACT_APP_GOOGLE_PAY_GATEWAY_MERCHANT_ID=pk_test_YOUR_STRIPE_KEY

# For Razorpay:
REACT_APP_GOOGLE_PAY_GATEWAY=razorpay
REACT_APP_GOOGLE_PAY_GATEWAY_MERCHANT_ID=rzp_test_YOUR_RAZORPAY_KEY
```

### Backend (application.properties):
```properties
# Google Pay Configuration
googlepay.merchant.id=BCR2DN4T2ZFQZQZQ
googlepay.environment=TEST
googlepay.gateway=stripe
googlepay.gateway.merchant.id=pk_test_YOUR_STRIPE_KEY
```

## Required Details Summary:

| Detail | Where to Get | Example |
|--------|-------------|---------|
| **Google Pay Merchant ID** | Google Pay Console | `BCR2DN4T2ZFQZQZQ` |
| **Payment Gateway** | Choose processor | `stripe` or `razorpay` |
| **Gateway Merchant ID** | Stripe/Razorpay Dashboard | `pk_test_...` or `rzp_test_...` |
| **Environment** | Set based on mode | `TEST` or `PRODUCTION` |

## Important Notes:

- ✅ **Google Pay Merchant ID**: Free to get from Google
- ✅ **Payment Processor**: Required for actual money processing
- ✅ **Test Mode**: Use test credentials for development
- ✅ **Live Mode**: Switch to live credentials for production
- ⚠️ **Fees**: Payment processor charges 2-3% per transaction