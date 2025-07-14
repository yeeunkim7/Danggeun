--
-- PostgreSQL database dump
--

-- Dumped from database version 15.13
-- Dumped by pg_dump version 15.13 (Homebrew)

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
-- Name: User; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."User" (
    user_id integer NOT NULL,
    user_nm character varying(50),
    user_password character varying(100),
    user_email character varying(100),
    user_phone character varying(50),
    user_createdat timestamp without time zone
);


ALTER TABLE public."User" OWNER TO postgres;

--
-- Name: User_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."User_user_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."User_user_id_seq" OWNER TO postgres;

--
-- Name: User_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."User_user_id_seq" OWNED BY public."User".user_id;


--
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
    user_add bigint NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE public.address OWNER TO postgres;

--
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    category_id integer NOT NULL,
    category_nm character varying(50)
);


ALTER TABLE public.category OWNER TO postgres;

--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_category_id_seq OWNER TO postgres;

--
-- Name: category_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.category_category_id_seq OWNED BY public.category.category_id;


--
-- Name: chat; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chat (
    chat_id integer NOT NULL,
    chat_createdat timestamp without time zone,
    buyer_id integer NOT NULL,
    seller_id integer NOT NULL,
    product_id integer NOT NULL
);


ALTER TABLE public.chat OWNER TO postgres;

--
-- Name: chat_chat_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.chat_chat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.chat_chat_id_seq OWNER TO postgres;

--
-- Name: chat_chat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.chat_chat_id_seq OWNED BY public.chat.chat_id;


--
-- Name: message; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.message (
    message_id integer NOT NULL,
    message_detail text,
    message_createdat timestamp without time zone,
    user_id integer NOT NULL,
    chat_id integer NOT NULL
);


ALTER TABLE public.message OWNER TO postgres;

--
-- Name: message_message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.message_message_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.message_message_id_seq OWNER TO postgres;

--
-- Name: message_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.message_message_id_seq OWNED BY public.message.message_id;


--
-- Name: product; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product (
    product_id integer NOT NULL,
    category_id integer NOT NULL,
    user_id integer NOT NULL,
    product_nm character varying(50),
    product_price numeric,
    product_detail text,
    product_state character varying(2) DEFAULT '00'::character varying,
    product_createdat timestamp without time zone,
    product_img_url character varying
);


ALTER TABLE public.product OWNER TO postgres;

--
-- Name: product_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.product_product_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.product_product_id_seq OWNER TO postgres;

--
-- Name: product_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.product_product_id_seq OWNED BY public.product.product_id;


--
-- Name: trade; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trade (
    product_id bigint NOT NULL,
    address oid,
    category_id bigint NOT NULL,
    product_created_at timestamp(6) without time zone,
    product_detail oid,
    product_img oid,
    product_nm character varying(50),
    product_price bigint,
    product_state character varying(2),
    product_title oid,
    user_id2 bigint NOT NULL
);


ALTER TABLE public.trade OWNER TO postgres;

--
-- Name: trade_product_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.trade ALTER COLUMN product_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.trade_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255),
    name character varying(255),
    provider character varying(255),
    provider_id character varying(255)
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: User user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User" ALTER COLUMN user_id SET DEFAULT nextval('public."User_user_id_seq"'::regclass);


--
-- Name: category category_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category ALTER COLUMN category_id SET DEFAULT nextval('public.category_category_id_seq'::regclass);


--
-- Name: chat chat_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat ALTER COLUMN chat_id SET DEFAULT nextval('public.chat_chat_id_seq'::regclass);


--
-- Name: message message_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message ALTER COLUMN message_id SET DEFAULT nextval('public.message_message_id_seq'::regclass);


--
-- Name: product product_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product ALTER COLUMN product_id SET DEFAULT nextval('public.product_product_id_seq'::regclass);


--
-- Name: 16433; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16433');


ALTER LARGE OBJECT 16433 OWNER TO postgres;

--
-- Name: 16434; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16434');


ALTER LARGE OBJECT 16434 OWNER TO postgres;

--
-- Name: 16435; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16435');


ALTER LARGE OBJECT 16435 OWNER TO postgres;

--
-- Name: 16436; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16436');


ALTER LARGE OBJECT 16436 OWNER TO postgres;

--
-- Name: 16437; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16437');


ALTER LARGE OBJECT 16437 OWNER TO postgres;

--
-- Name: 16438; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16438');


ALTER LARGE OBJECT 16438 OWNER TO postgres;

--
-- Name: 16439; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16439');


ALTER LARGE OBJECT 16439 OWNER TO postgres;

--
-- Name: 16440; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16440');


ALTER LARGE OBJECT 16440 OWNER TO postgres;

--
-- Name: 16441; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16441');


ALTER LARGE OBJECT 16441 OWNER TO postgres;

--
-- Name: 16442; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16442');


ALTER LARGE OBJECT 16442 OWNER TO postgres;

--
-- Name: 16444; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16444');


ALTER LARGE OBJECT 16444 OWNER TO postgres;

--
-- Name: 16445; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16445');


ALTER LARGE OBJECT 16445 OWNER TO postgres;

--
-- Name: 16447; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16447');


ALTER LARGE OBJECT 16447 OWNER TO postgres;

--
-- Name: 16448; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16448');


ALTER LARGE OBJECT 16448 OWNER TO postgres;

--
-- Name: 16449; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16449');


ALTER LARGE OBJECT 16449 OWNER TO postgres;

--
-- Name: 16450; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16450');


ALTER LARGE OBJECT 16450 OWNER TO postgres;

--
-- Name: 16451; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16451');


ALTER LARGE OBJECT 16451 OWNER TO postgres;

--
-- Name: 16452; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16452');


ALTER LARGE OBJECT 16452 OWNER TO postgres;

--
-- Name: 16453; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16453');


ALTER LARGE OBJECT 16453 OWNER TO postgres;

--
-- Name: 16454; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16454');


ALTER LARGE OBJECT 16454 OWNER TO postgres;

--
-- Name: 16455; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16455');


ALTER LARGE OBJECT 16455 OWNER TO postgres;

--
-- Name: 16456; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16456');


ALTER LARGE OBJECT 16456 OWNER TO postgres;

--
-- Name: 16457; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16457');


ALTER LARGE OBJECT 16457 OWNER TO postgres;

--
-- Name: 16458; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16458');


ALTER LARGE OBJECT 16458 OWNER TO postgres;

--
-- Name: 16459; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16459');


ALTER LARGE OBJECT 16459 OWNER TO postgres;

--
-- Name: 16460; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16460');


ALTER LARGE OBJECT 16460 OWNER TO postgres;

--
-- Name: 16461; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16461');


ALTER LARGE OBJECT 16461 OWNER TO postgres;

--
-- Name: 16462; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16462');


ALTER LARGE OBJECT 16462 OWNER TO postgres;

--
-- Name: 16463; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16463');


ALTER LARGE OBJECT 16463 OWNER TO postgres;

--
-- Name: 16464; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16464');


ALTER LARGE OBJECT 16464 OWNER TO postgres;

--
-- Name: 16465; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16465');


ALTER LARGE OBJECT 16465 OWNER TO postgres;

--
-- Name: 16466; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16466');


ALTER LARGE OBJECT 16466 OWNER TO postgres;

--
-- Name: 16467; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16467');


ALTER LARGE OBJECT 16467 OWNER TO postgres;

--
-- Name: 16468; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16468');


ALTER LARGE OBJECT 16468 OWNER TO postgres;

--
-- Name: 16469; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16469');


ALTER LARGE OBJECT 16469 OWNER TO postgres;

--
-- Name: 16470; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16470');


ALTER LARGE OBJECT 16470 OWNER TO postgres;

--
-- Name: 16471; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16471');


ALTER LARGE OBJECT 16471 OWNER TO postgres;

--
-- Name: 16472; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16472');


ALTER LARGE OBJECT 16472 OWNER TO postgres;

--
-- Name: 16473; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16473');


ALTER LARGE OBJECT 16473 OWNER TO postgres;

--
-- Name: 16474; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16474');


ALTER LARGE OBJECT 16474 OWNER TO postgres;

--
-- Name: 16475; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16475');


ALTER LARGE OBJECT 16475 OWNER TO postgres;

--
-- Name: 16476; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16476');


ALTER LARGE OBJECT 16476 OWNER TO postgres;

--
-- Name: 16477; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16477');


ALTER LARGE OBJECT 16477 OWNER TO postgres;

--
-- Name: 16478; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16478');


ALTER LARGE OBJECT 16478 OWNER TO postgres;

--
-- Name: 16479; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16479');


ALTER LARGE OBJECT 16479 OWNER TO postgres;

--
-- Name: 16480; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16480');


ALTER LARGE OBJECT 16480 OWNER TO postgres;

--
-- Name: 16481; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16481');


ALTER LARGE OBJECT 16481 OWNER TO postgres;

--
-- Name: 16482; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16482');


ALTER LARGE OBJECT 16482 OWNER TO postgres;

--
-- Name: 16483; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16483');


ALTER LARGE OBJECT 16483 OWNER TO postgres;

--
-- Name: 16484; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16484');


ALTER LARGE OBJECT 16484 OWNER TO postgres;

--
-- Name: 16485; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16485');


ALTER LARGE OBJECT 16485 OWNER TO postgres;

--
-- Name: 16486; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16486');


ALTER LARGE OBJECT 16486 OWNER TO postgres;

--
-- Name: 16487; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16487');


ALTER LARGE OBJECT 16487 OWNER TO postgres;

--
-- Name: 16488; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16488');


ALTER LARGE OBJECT 16488 OWNER TO postgres;

--
-- Name: 16489; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16489');


ALTER LARGE OBJECT 16489 OWNER TO postgres;

--
-- Name: 16490; Type: BLOB; Schema: -; Owner: postgres
--

SELECT pg_catalog.lo_create('16490');


ALTER LARGE OBJECT 16490 OWNER TO postgres;

--
-- Data for Name: User; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."User" (user_id, user_nm, user_password, user_email, user_phone, user_createdat) FROM stdin;
\.


--
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.address (user_add, user_id) FROM stdin;
\.


--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.category (category_id, category_nm) FROM stdin;
\.


--
-- Data for Name: chat; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chat (chat_id, chat_createdat, buyer_id, seller_id, product_id) FROM stdin;
\.


--
-- Data for Name: message; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.message (message_id, message_detail, message_createdat, user_id, chat_id) FROM stdin;
\.


--
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product (product_id, category_id, user_id, product_nm, product_price, product_detail, product_state, product_createdat, product_img_url) FROM stdin;
\.


--
-- Data for Name: trade; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.trade (product_id, address, category_id, product_created_at, product_detail, product_img, product_nm, product_price, product_state, product_title, user_id2) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, name, provider, provider_id) FROM stdin;
\.


--
-- Name: User_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."User_user_id_seq"', 1, false);


--
-- Name: category_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.category_category_id_seq', 1, false);


--
-- Name: chat_chat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.chat_chat_id_seq', 1, false);


--
-- Name: message_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.message_message_id_seq', 1, false);


--
-- Name: product_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_product_id_seq', 1, false);


--
-- Name: trade_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.trade_product_id_seq', 1, false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- Data for Name: BLOBS; Type: BLOBS; Schema: -; Owner: -
--

BEGIN;

SELECT pg_catalog.lo_open('16433', 131072);
SELECT pg_catalog.lowrite(0, '\x34');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16434', 131072);
SELECT pg_catalog.lowrite(0, '\x33');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16435', 131072);
SELECT pg_catalog.lowrite(0, '\x35');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16436', 131072);
SELECT pg_catalog.lowrite(0, '\x34');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16437', 131072);
SELECT pg_catalog.lowrite(0, '\x32');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16438', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16439', 131072);
SELECT pg_catalog.lowrite(0, '\x32');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16440', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16441', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16442', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16444', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16445', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16447', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16448', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16449', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16450', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16451', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16452', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16453', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16454', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16455', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16456', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16457', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16458', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16459', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16460', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16461', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16462', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16463', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16464', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16465', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16466', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16467', 131072);
SELECT pg_catalog.lowrite(0, '\x32');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16468', 131072);
SELECT pg_catalog.lowrite(0, '\x32');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16469', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16470', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16471', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16472', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16473', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16474', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16475', 131072);
SELECT pg_catalog.lowrite(0, '\xe282a9');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16476', 131072);
SELECT pg_catalog.lowrite(0, '\xe282a9');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16477', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16478', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16479', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16480', 131072);
SELECT pg_catalog.lowrite(0, '\x31');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16481', 131072);
SELECT pg_catalog.lowrite(0, '\x3138');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16482', 131072);
SELECT pg_catalog.lowrite(0, '\x3138');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16483', 131072);
SELECT pg_catalog.lowrite(0, '\x3238');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16484', 131072);
SELECT pg_catalog.lowrite(0, '\x3238');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16485', 131072);
SELECT pg_catalog.lowrite(0, '\xec98a4eba5b4ebafb8');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16486', 131072);
SELECT pg_catalog.lowrite(0, '\xeab28cec8b9ceab88020eb82b4ec9aa9ec9d8420ec9e85eba0a520ec9e98ed9688ec96b4ec9a942e');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16487', 131072);
SELECT pg_catalog.lowrite(0, '\x3138');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16488', 131072);
SELECT pg_catalog.lowrite(0, '\x3236');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16489', 131072);
SELECT pg_catalog.lowrite(0, '\x34');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('16490', 131072);
SELECT pg_catalog.lowrite(0, '\x33');
SELECT pg_catalog.lo_close(0);

COMMIT;

--
-- Name: User User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (user_id);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (user_id);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: chat chat_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT chat_pkey PRIMARY KEY (chat_id);


--
-- Name: message message_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (message_id);


--
-- Name: product product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (product_id);


--
-- Name: trade trade_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trade
    ADD CONSTRAINT trade_pkey PRIMARY KEY (product_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: address fk_address_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES public."User"(user_id);


--
-- Name: chat fk_chat_buyer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT fk_chat_buyer FOREIGN KEY (buyer_id) REFERENCES public."User"(user_id);


--
-- Name: chat fk_chat_product; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT fk_chat_product FOREIGN KEY (product_id) REFERENCES public.product(product_id);


--
-- Name: chat fk_chat_seller; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT fk_chat_seller FOREIGN KEY (seller_id) REFERENCES public."User"(user_id);


--
-- Name: product fk_product_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES public.category(category_id);


--
-- Name: product fk_product_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk_product_user FOREIGN KEY (user_id) REFERENCES public."User"(user_id);


--
-- PostgreSQL database dump complete
--

