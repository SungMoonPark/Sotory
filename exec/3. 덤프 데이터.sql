--
-- PostgreSQL database dump
--

-- Dumped from database version 15.12 (Debian 15.12-1.pgdg120+1)
-- Dumped by pg_dump version 16.8

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: auth_tokens; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.auth_tokens (
    id bigint NOT NULL,
    app_version character varying(255),
    device_id character varying(255),
    device_name character varying(255),
    expires_at timestamp(6) without time zone,
    issued_at timestamp(6) without time zone,
    refresh_token character varying(255),
    valid boolean NOT NULL,
    version bigint,
    auth_user_id uuid NOT NULL
);


ALTER TABLE public.auth_tokens OWNER TO root;

--
-- Name: auth_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.auth_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.auth_tokens_id_seq OWNER TO root;

--
-- Name: auth_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.auth_tokens_id_seq OWNED BY public.auth_tokens.id;


--
-- Name: auth_users; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.auth_users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    is_deleted boolean NOT NULL,
    provider character varying(255) NOT NULL,
    provider_user_id character varying(255) NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.auth_users OWNER TO root;

--
-- Name: budget; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.budget (
    budget_id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone NOT NULL,
    budget integer NOT NULL,
    is_deleted boolean NOT NULL,
    month date NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.budget OWNER TO root;

--
-- Name: card; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.card (
    card_id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    card_issuer_code character varying(4) NOT NULL,
    card_issuer_name character varying(20) NOT NULL,
    card_name character varying(100) NOT NULL,
    card_no character varying(20) NOT NULL,
    is_deleted boolean NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.card OWNER TO root;

--
-- Name: diary_card; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.diary_card (
    diary_card_id uuid NOT NULL,
    weather character varying(255) DEFAULT 'DEFAULT'::character varying,
    created_at date,
    img_src character varying(255),
    summary character varying(255),
    user_id uuid NOT NULL,
    CONSTRAINT diary_card_weather_check CHECK (((weather)::text = ANY ((ARRAY['RAIN'::character varying, 'SNOW'::character varying, 'DEFAULT'::character varying])::text[])))
);


ALTER TABLE public.diary_card OWNER TO root;

--
-- Name: payment_diary; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.payment_diary (
    payment_diary_id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone NOT NULL,
    category_name character varying(255),
    diary text,
    is_deleted boolean NOT NULL,
    is_user_added boolean NOT NULL,
    merchant_name character varying(255),
    payment_id uuid,
    transaction_balance integer,
    transaction_date date,
    transaction_time time(6) without time zone,
    user_id uuid NOT NULL
);


ALTER TABLE public.payment_diary OWNER TO root;

--
-- Name: users; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    birthday character varying(255),
    gender character varying(255),
    nickname character varying(50) NOT NULL,
    user_key character varying(255),
    CONSTRAINT users_gender_check CHECK (((gender)::text = ANY ((ARRAY['M'::character varying, 'F'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO root;

--
-- Name: wordclouds; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.wordclouds (
    wordcloud_id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    last_modified_at timestamp(6) without time zone NOT NULL,
    diary_count integer NOT NULL,
    image_url text NOT NULL,
    is_deleted boolean NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    year_month character varying(7) NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.wordclouds OWNER TO root;

--
-- Name: auth_tokens id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_tokens ALTER COLUMN id SET DEFAULT nextval('public.auth_tokens_id_seq'::regclass);


--
-- Name: auth_tokens auth_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_tokens
    ADD CONSTRAINT auth_tokens_pkey PRIMARY KEY (id);


--
-- Name: auth_users auth_users_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_users
    ADD CONSTRAINT auth_users_pkey PRIMARY KEY (id);


--
-- Name: budget budget_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.budget
    ADD CONSTRAINT budget_pkey PRIMARY KEY (budget_id);


--
-- Name: card card_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.card
    ADD CONSTRAINT card_pkey PRIMARY KEY (card_id);


--
-- Name: diary_card diary_card_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.diary_card
    ADD CONSTRAINT diary_card_pkey PRIMARY KEY (diary_card_id);


--
-- Name: payment_diary payment_diary_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.payment_diary
    ADD CONSTRAINT payment_diary_pkey PRIMARY KEY (payment_diary_id);


--
-- Name: auth_tokens uk_cdqhpyn5b6v3iwo3iucyut2gc; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_tokens
    ADD CONSTRAINT uk_cdqhpyn5b6v3iwo3iucyut2gc UNIQUE (auth_user_id);


--
-- Name: auth_users uk_myl0r4rbb6wj9xiu6vv6v5ger; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_users
    ADD CONSTRAINT uk_myl0r4rbb6wj9xiu6vv6v5ger UNIQUE (user_id);


--
-- Name: auth_users uk_siqaproe2otvbejkkl6ecs486; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_users
    ADD CONSTRAINT uk_siqaproe2otvbejkkl6ecs486 UNIQUE (provider_user_id);


--
-- Name: wordclouds uk_user_yearmonth; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wordclouds
    ADD CONSTRAINT uk_user_yearmonth UNIQUE (user_id, year_month);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: wordclouds wordclouds_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wordclouds
    ADD CONSTRAINT wordclouds_pkey PRIMARY KEY (wordcloud_id);


--
-- Name: idx_diary_created_at; Type: INDEX; Schema: public; Owner: root
--

CREATE INDEX idx_diary_created_at ON public.diary_card USING btree (created_at);


--
-- Name: idx_payment_diary_transaction_date; Type: INDEX; Schema: public; Owner: root
--

CREATE INDEX idx_payment_diary_transaction_date ON public.payment_diary USING btree (transaction_date);


--
-- Name: auth_tokens fk2c1784kl82grr2b19ehwexaj7; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_tokens
    ADD CONSTRAINT fk2c1784kl82grr2b19ehwexaj7 FOREIGN KEY (auth_user_id) REFERENCES public.auth_users(id);


--
-- Name: diary_card fkar2vobxf6n6c5g60wsj9xkk2g; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.diary_card
    ADD CONSTRAINT fkar2vobxf6n6c5g60wsj9xkk2g FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: wordclouds fkd129ahf8is1dvlcxo1t0iuhdc; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.wordclouds
    ADD CONSTRAINT fkd129ahf8is1dvlcxo1t0iuhdc FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: auth_users fkrpvvat76cqj7gfiyhoso7skyq; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.auth_users
    ADD CONSTRAINT fkrpvvat76cqj7gfiyhoso7skyq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: payment_diary fktklwypnugn6w9db7bchcrjxbj; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.payment_diary
    ADD CONSTRAINT fktklwypnugn6w9db7bchcrjxbj FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

