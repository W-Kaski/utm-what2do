import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 8000
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API request failed', error);
    return Promise.reject(error);
  }
);

export default apiClient;
