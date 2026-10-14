
-- V2: Sample catalogue data for local development

INSERT INTO categories (name, slug, description)
VALUES
    ('London Travel Guides', 'london-travel-guides',
     'Digital guides for exploring London'),
    ('London Maps', 'london-maps',
     'Maps and itineraries for London visitors');

INSERT INTO products (
    category_id,
    name,
    slug,
    short_description,
    description,
    product_type,
    price_minor,
    currency,
    status
)
VALUES
    (
        (SELECT id FROM categories
         WHERE slug = 'london-travel-guides'),
        'Hidden London Walking Guide',
        'hidden-london-walking-guide',
        'Discover lesser-known London streets and landmarks.',
        'A sample digital walking guide for local development.',
        'DIGITAL',
        599,
        'GBP',
        'ACTIVE'
    ),
    (
        (SELECT id FROM categories
         WHERE slug = 'london-maps'),
        'London Photography Spot Map',
        'london-photography-spot-map',
        'Explore photography locations across London.',
        'A sample photography map for local development.',
        'DIGITAL',
        899,
        'GBP',
        'ACTIVE'
    ),
    (
        (SELECT id FROM categories
         WHERE slug = 'london-travel-guides'),
        'Weekend London Itinerary',
        'weekend-london-itinerary',
        'A sample two-day London itinerary.',
        'A draft product used to verify public visibility rules.',
        'DIGITAL',
        499,
        'GBP',
        'DRAFT'
    );
