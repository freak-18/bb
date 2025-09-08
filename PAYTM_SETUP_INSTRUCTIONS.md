# Paytm Integration Setup Instructions

## Step 1: Create Paytm Business Account

1. Visit: https://business.paytm.com/
2. Click "Sign Up" and create merchant account
3. Complete KYC verification (required for live payments)
4. Verify your business details

## Step 2: Get API Credentials

1. Login to Paytm Business Dashboard
2. Go to Developer Settings → API Keys
3. Generate Test Credentials (for development)
4. Generate Live Credentials (for production)

## Step 3: Update Your Application

### Frontend (.env file):
```
REACT_APP_PAYTM_MID=YOUR_MERCHANT_ID
REACT_APP_PAYTM_WEBSITE=WEBSTAGING
REACT_APP_PAYTM_CALLBACK_URL=http://localhost:3000/payment/callback
```

### Backend (application.properties):
```
paytm.merchant.id=YOUR_MERCHANT_ID
paytm.merchant.key=YOUR_MERCHANT_KEY
paytm.website=WEBSTAGING
paytm.callback.url=http://localhost:8080/api/payment/callback
```

## Step 4: Test Integration

1. Use Paytm test credentials
2. Test with small amounts (₹1, ₹10)
3. Use test cards provided by Paytm
4. Test card: 4111 1111 1111 1111

## Step 5: Go Live

1. Complete business verification
2. Update to live credentials
3. Change WEBSITE to "DEFAULT"
4. Test with small amounts first

## Important Notes

- Never expose MERCHANT_KEY in frontend
- Use WEBSTAGING for development
- Verify payments on backend
- Handle payment failures gracefully
- Paytm charges 2-3% transaction fee