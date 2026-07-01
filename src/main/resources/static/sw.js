const CACHE_NAME = 'campshare-v1';
const urlsToCache = [
  '/',
  '/login.html',
  '/register.html',
  '/dashboard.html',
  '/detail-barang.html',
  '/form-barang.html',
  '/papan-request.html',
  '/profil.html',
  '/roomchat.html',
  '/transaksi.html',
  '/css/style.css',        // jika ada file CSS eksternal
  '/js/app.js',            // jika ada file JS eksternal
  '/manifest.json',
  '/icons/icon-192x192.png',
  '/icons/icon-512x512.png'
];

// Install service worker – cache aset awal
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(urlsToCache))
  );
});

// Activate – hapus cache lama jika ada versi baru
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(name => {
          if (name !== CACHE_NAME) {
            return caches.delete(name);
          }
        })
      );
    })
  );
});

// Fetch – coba dari cache, jika tidak ada ambil dari network
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => response || fetch(event.request))
  );
});