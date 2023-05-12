import React from "react";
import { useSearchParams } from "react-router-dom";

export default function useSearchParamQuery(): [
	string,
	() => boolean,
	(query: string) => void
] {
	let [searchParams, setSearchParams] = useSearchParams();
	const query = searchParams.get("q") ?? "";

	const isSeeingResults = () => {
		return searchParams.has("q") && searchParams.get("q") != "";
	};
	const setQuery = (query: string) => {
		setSearchParams({ q: query.trim() });
	};

	return [query, isSeeingResults, setQuery];
}
