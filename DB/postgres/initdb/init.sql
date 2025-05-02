CREATE TABLE budgets (
    budget_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    month DATE NOT NULL,
    budget INTEGER NOT NULL,
    created_at DATE NOT NULL
);
