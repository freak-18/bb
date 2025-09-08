# Deployment Guide - Vercel + Render

## 🚀 Quick Deployment Steps

### 1. Backend Deployment (Render)

1. **Push to GitHub**:
   ```bash
   git add .
   git commit -m "Ready for deployment"
   git push origin main
   ```

2. **Deploy on Render**:
   - Visit: https://render.com/
   - Click "New" → "Web Service"
   - Connect your GitHub repository
   - Select `springapp` folder as root directory
   - Use these settings:
     - **Build Command**: `./mvnw clean package -DskipTests`
     - **Start Command**: `java -jar target/*.jar`
     - **Environment**: `SPRING_PROFILES_ACTIVE=prod`

3. **Add Database**:
   - Click "New" → "PostgreSQL"
   - Name: `zenstay-db`
   - Copy connection string to your web service environment variables

### 2. Frontend Deployment (Vercel)

1. **Deploy on Vercel**:
   - Visit: https://vercel.com/
   - Click "New Project"
   - Import your GitHub repository
   - Select `reactapp` folder as root directory
   - Add environment variables:
     ```
     REACT_APP_API_URL=https://your-render-app.onrender.com
     REACT_APP_GOOGLE_PAY_MERCHANT_ID=BCR2DN4T2ZFQZQZQ
     REACT_APP_GOOGLE_PAY_ENVIRONMENT=TEST
     ```

2. **Update CORS**:
   - After Vercel deployment, update `application-prod.properties`:
   ```
   cors.allowed.origins=https://your-vercel-app.vercel.app
   ```

## 🔧 Environment Variables

### Render (Backend):
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://user:pass@host:port/db
GOOGLE_PAY_MERCHANT_ID=BCR2DN4T2ZFQZQZQ
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### Vercel (Frontend):
```
REACT_APP_API_URL=https://your-render-app.onrender.com
REACT_APP_GOOGLE_PAY_MERCHANT_ID=BCR2DN4T2ZFQZQZQ
REACT_APP_GOOGLE_PAY_ENVIRONMENT=TEST
```

## 📝 Post-Deployment Checklist

- [ ] Backend API responding at `/api/rooms`
- [ ] Frontend loading without errors
- [ ] Booking creation working
- [ ] Payment page accessible
- [ ] Google Pay button visible
- [ ] Test payment working

## 🔗 URLs After Deployment

- **Frontend**: `https://your-app.vercel.app`
- **Backend**: `https://your-app.onrender.com`
- **API Docs**: `https://your-app.onrender.com/api/rooms`

## 🐛 Common Issues

1. **CORS Error**: Update `cors.allowed.origins` in production properties
2. **Database Connection**: Check DATABASE_URL format
3. **Build Fails**: Ensure Java 17 is specified in Render
4. **API Not Found**: Update REACT_APP_API_URL in Vercel

## 💡 Free Tier Limitations

- **Render**: 750 hours/month, sleeps after 15min inactivity
- **Vercel**: 100GB bandwidth, 6000 minutes build time
- **PostgreSQL**: 1GB storage, 97 connection limit