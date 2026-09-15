-- ============================================================
-- FOODREELS
-- PHASE 10
-- DATABASE HARDENING
-- ============================================================


-- ============================================================
-- RESTAURANT OWNER COLUMN
-- ============================================================

ALTER TABLE restaurants
ADD COLUMN IF NOT EXISTS owner_id BIGINT;


-- ============================================================
-- RESTAURANT OWNER INDEX
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_restaurants_owner
ON restaurants(owner_id);


-- ============================================================
-- ORDER INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_orders_user_created
ON orders(user_id, created_at);


CREATE INDEX IF NOT EXISTS idx_orders_restaurant_created
ON orders(restaurant_id, created_at);


CREATE INDEX IF NOT EXISTS idx_orders_user_status
ON orders(user_id, status);


CREATE INDEX IF NOT EXISTS idx_orders_restaurant_status
ON orders(restaurant_id, status);


-- ============================================================
-- ORDER ITEM INDEXES
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_order_items_order
ON order_items(order_id);


CREATE INDEX IF NOT EXISTS idx_order_items_food
ON order_items(food_id);