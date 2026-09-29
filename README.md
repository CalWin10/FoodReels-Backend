```html
<h1 align="center">🍔 FoodReels</h1>

<p align="center">
  <strong>Short-form food discovery and ordering platform</strong>
</p>

<p align="center">
  Discover food through reels → Explore restaurants → Find dishes → Order
</p>

<hr>

<h2>🚀 Overview</h2>

<p>
  <strong>FoodReels</strong> is a full-stack food discovery platform built around
  short-form food videos.
</p>

<p>Users can:</p>

<ul>
  <li>🎬 Discover food through reels</li>
  <li>❤️ Like and save content</li>
  <li>💬 Comment on food content</li>
  <li>🔍 Search foods, restaurants and reels</li>
  <li>📍 Discover nearby food</li>
  <li>🛒 Add food to a cart</li>
  <li>📦 Place and track orders</li>
  <li>🧠 Receive personalized recommendations</li>
</ul>

<hr>

<h2>🛠️ Tech Stack</h2>

<h3>Backend</h3>

<ul>
  <li>Java 25</li>
  <li>Spring Boot</li>
  <li>Spring Security</li>
  <li>JWT</li>
  <li>Spring Data JPA</li>
  <li>Hibernate</li>
  <li>PostgreSQL</li>
  <li>Redis</li>
  <li>Maven</li>
  <li>Flyway</li>
</ul>

<h3>Frontend</h3>

<ul>
  <li>React</li>
  <li>TypeScript</li>
  <li>Vite</li>
  <li>Tailwind CSS</li>
  <li>React Router</li>
  <li>Axios</li>
  <li>Vitest</li>
  <li>Testing Library</li>
</ul>

<h3>Tools</h3>

<ul>
  <li>Git &amp; GitHub</li>
  <li>Postman</li>
  <li>Swagger / OpenAPI</li>
  <li>VS Code</li>
  <li>Antigravity</li>
</ul>

<hr>

<h2>🏗️ Architecture</h2>

<pre>
                    FoodReels
                        │
             ┌──────────┴──────────┐
             │                     │
        React Frontend       Spring Boot Backend
             │                     │
             │                Service Layer
             │                     │
             │                Repository
             │                     │
             │              ┌──────┴──────┐
             │              │             │
             │         PostgreSQL        Redis
             │
             └──────────── REST API ────────────┘
</pre>

<hr>

<h2>📁 Project Structure</h2>

<pre>
FoodReels/
├── backend/
├── frontend/
├── docs/
├── .github/
├── .gitignore
└── README.md
</pre>

<hr>

<h2>✨ Features</h2>

<h3>Backend</h3>

<ul>
  <li>✅ Authentication and JWT authorization</li>
  <li>✅ Role-based access control</li>
  <li>✅ User, restaurant, food and reel management</li>
  <li>✅ Reel feed and personalized feed</li>
  <li>✅ Likes, saves and comments</li>
  <li>✅ Watch history and preferences</li>
  <li>✅ Search and filtering</li>
  <li>✅ Nearby discovery</li>
  <li>✅ Cart and ordering</li>
  <li>✅ Order lifecycle management</li>
  <li>✅ Redis caching</li>
  <li>✅ Flyway migrations</li>
  <li>✅ Global error handling</li>
  <li>✅ Swagger/OpenAPI</li>
  <li>✅ Automated testing</li>
</ul>

<h3>Frontend</h3>

<ul>
  <li>✅ React + TypeScript foundation</li>
  <li>✅ Responsive application shell</li>
  <li>✅ Authentication foundation</li>
  <li>🚧 Consumer UI and feature development in progress</li>
</ul>

<hr>

<h2>🎬 Core Experience</h2>

<pre>
Watch Reel
    ↓
Discover Food
    ↓
Explore Restaurant
    ↓
View Food
    ↓
Add to Cart
    ↓
Checkout
    ↓
Place Order
</pre>

<hr>

<h2>▶️ Local Development</h2>

<h3>Backend</h3>

<pre>
cd backend
.\mvnw.cmd spring-boot:run
</pre>

<p>
  Backend:
  <code>http://localhost:8080</code>
</p>

<h3>Frontend</h3>

<pre>
cd frontend
npm install
npm run dev
</pre>

<hr>

<h2>🧪 Testing</h2>

<h3>Backend</h3>

<pre>
cd backend
.\mvnw.cmd test
</pre>

<h3>Frontend</h3>

<pre>
npm test -- --run
</pre>

<h3>Frontend Build</h3>

<pre>
npm run build
</pre>

<hr>

<h2>📚 API Documentation</h2>

<p>
  <strong>Swagger UI:</strong>
  <code>http://localhost:8080/swagger-ui.html</code>
</p>

<p>
  <strong>OpenAPI:</strong>
  <code>http://localhost:8080/v3/api-docs</code>
</p>

<hr>

<h2>🌿 Git Workflow</h2>

<pre>
main
  │
  └── dev
       │
       └── feature/*
</pre>

<p>
  Development work is done on <code>dev</code>.
</p>

<pre>
git switch dev
git pull
git switch -c feature/reels
git add .
git commit -m "feat: improve reel experience"
git push -u origin feature/reels
</pre>

<hr>

<h2>📌 Current Status</h2>

<h3>Backend</h3>

<p>
  ✅ Core backend<br>
  ✅ Authentication &amp; authorization<br>
  ✅ Reels &amp; engagement<br>
  ✅ Personalized recommendations<br>
  ✅ Redis caching<br>
  ✅ Search<br>
  ✅ Location discovery<br>
  ✅ Ordering<br>
  ✅ Production hardening<br>
  ✅ Automated testing
</p>

<h3>Frontend</h3>

<p>
  ✅ React + TypeScript foundation<br>
  ✅ Responsive application shell<br>
  ✅ Authentication foundation<br>
  🚧 UI and feature development in progress
</p>

<hr>

<h2>📖 Documentation</h2>

<p>
  Detailed architecture, API documentation, phase documentation, testing
  information, and engineering notes are available in:
</p>

<p>
  <code>docs/</code>
</p>

<hr>

<h2>👨‍💻 Developer</h2>

<p>
  <strong>Calwin Samuel</strong>
</p>

<p>
  GitHub:
  <a href="https://github.com/CalWin10">Calwin10</a>
</p>

<hr>

<h2>📄 License</h2>

<p>
  MIT License.
</p>

<hr>

<p align="center">
  <strong>Discover food. Watch. Decide. Order. 🍔</strong>
</p>
```
