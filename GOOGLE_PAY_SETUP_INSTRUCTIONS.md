# Google Pay Integration Setup Instructions

## Step 1: Register with Google Pay

1. Visit: https://developers.google.com/pay/api
2. Create a Google Cloud Project
3. Enable Google Pay API
4. Get your Merchant ID from Google Pay Console

## Step 2: Choose Payment Processor

Google Pay requires a payment processor:
- **Stripe** (Recommended for India)
- **Square** 
- **Braintree**
- **Adyen**

## Step 3: Update Configuration

### Frontend (.env file):
```
REACT_APP_GOOGLE_PAY_MERCHANT_ID=YOUR_MERCHANT_ID
REACT_APP_GOOGLE_PAY_ENVIRONMENT=TEST
REACT_APP_GOOGLE_PAY_GATEWAY=stripe
REACT_APP_GOOGLE_PAY_GATEWAY_MERCHANT_ID=YOUR_STRIPE_MERCHANT_ID
```

### Backend (application.properties):
```
googlepay.merchant.id=YOUR_MERCHANT_ID
googlepay.environment=TEST
googlepay.gateway=stripe
googlepay.gateway.merchant.id=YOUR_STRIPE_MERCHANT_ID
```

## Step 4: Test Integration

1. Use test environment (TEST)
2. Test with Google Pay button
3. Use test cards from your payment processor

## Step 5: Go Live

1. Complete Google Pay verification
2. Update environment to PRODUCTION
3. Use live payment processor credentials

## Benefits of Google Pay

✅ **Secure**: Tokenized payments, no card details stored
✅ **Fast**: One-tap checkout experience  
✅ **Popular**: Widely used in India
✅ **Mobile-First**: Optimized for mobile devices
✅ **Multiple Cards**: Users can choose from saved cards

## Important Notes

- Requires HTTPS in production
- Works best on mobile devices
- Needs payment processor integration
- 2-3% transaction fees (via processor)