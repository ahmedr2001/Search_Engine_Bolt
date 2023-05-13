import React, { useEffect, useState } from "react";
import useSearchParamQuery from "../hooks/useSearchParamQuery";
import resultType from "../types/resultType";
import Result from "./Result";

type Props = {};
interface ErrorType {
	message: string;
	code: number;
}
export default function ResultsList({}: Props) {
	const [query, isSeeingResults, setQuery] = useSearchParamQuery();
	const [isLoading, setIsLoading] = useState(false);
	const [results, setResults] = useState<string[]>();
	const [error, setError] = useState("");

	useEffect(() => {
		async function fetchData() {
			setIsLoading(true);

			try {
				const response = await fetch(
					`http://localhost:8080/search?q=${query}`
				);
				const data = await response.json();
				console.log(data);
				setResults(data);
			} catch (error) {
				console.error(error);
				setError((error as ErrorType).message);
			}

			setIsLoading(false);
		}

		fetchData();
	}, [query]);

	if (isLoading) return <div>waiting.....</div>;
	else if (error) return <div>Error ... {error}</div>;
	else {
		return (
			<div className="flex flex-col w-full pt-16 gap-5 text-highlight">
				{results?.map((res: string, index) => {
					return <Result key={index} url={res} />;
				})}
			</div>
		);
	}
}
