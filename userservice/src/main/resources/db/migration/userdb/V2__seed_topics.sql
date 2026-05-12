-- Parent categories
INSERT INTO topics (name, slug, sort_order) VALUES
    ('Technology', 'technology', 1),
    ('Arts & Entertainment', 'arts-entertainment', 2),
    ('Business & Entrepreneurship', 'business-entrepreneurship', 3),
    ('Education & Motivation', 'education-motivation', 4),
    ('Health & Wellness', 'health-wellness', 5),
    ('Social Impact', 'social-impact', 6),
    ('Sports & Fitness', 'sports-fitness', 7);

-- Technology subtopics
INSERT INTO topics (name, slug, parent_id, sort_order)
SELECT name, slug, (SELECT id FROM topics WHERE slug = 'technology'), sort_order
FROM (VALUES
    ('Artificial Intelligence', 'ai', 1),
    ('Cybersecurity', 'cybersecurity', 2),
    ('Web Development', 'web-development', 3),
    ('Mobile Development', 'mobile-development', 4),
    ('Cloud & DevOps', 'cloud-devops', 5),
    ('Data Science', 'data-science', 6),
    ('Blockchain & Web3', 'blockchain-web3', 7)
) AS t(name, slug, sort_order);

-- Arts subtopics
INSERT INTO topics (name, slug, parent_id, sort_order)
SELECT name, slug, (SELECT id FROM topics WHERE slug = 'arts-entertainment'), sort_order
FROM (VALUES
    ('Stand-up Comedy', 'standup-comedy', 1),
    ('Music & Singing', 'music-singing', 2),
    ('Dance', 'dance', 3),
    ('Photography', 'photography', 4),
    ('Film & Storytelling', 'film-storytelling', 5),
    ('Anchoring & Hosting', 'anchoring-hosting', 6),
    ('Poetry & Spoken Word', 'poetry-spoken-word', 7)
) AS t(name, slug, sort_order);

-- Business subtopics
INSERT INTO topics (name, slug, parent_id, sort_order)
SELECT name, slug, (SELECT id FROM topics WHERE slug = 'business-entrepreneurship'), sort_order
FROM (VALUES
    ('Startups & Founding', 'startups', 1),
    ('Marketing & Growth', 'marketing-growth', 2),
    ('Finance & Investing', 'finance-investing', 3),
    ('Leadership & Management', 'leadership', 4),
    ('Product Management', 'product-management', 5)
) AS t(name, slug, sort_order);

-- Education subtopics
INSERT INTO topics (name, slug, parent_id, sort_order)
SELECT name, slug, (SELECT id FROM topics WHERE slug = 'education-motivation'), sort_order
FROM (VALUES
    ('Motivational Speaking', 'motivational-speaking', 1),
    ('Career Guidance', 'career-guidance', 2),
    ('Life Coaching', 'life-coaching', 3),
    ('Academic Tutoring', 'academic-tutoring', 4)
) AS t(name, slug, sort_order);

-- Health subtopics
INSERT INTO topics (name, slug, parent_id, sort_order)
SELECT name, slug, (SELECT id FROM topics WHERE slug = 'health-wellness'), sort_order
FROM (VALUES
    ('Yoga & Meditation', 'yoga-meditation', 1),
    ('Mental Health', 'mental-health', 2),
    ('Nutrition & Diet', 'nutrition-diet', 3),
    ('Fitness Training', 'fitness-training', 4)
) AS t(name, slug, sort_order);