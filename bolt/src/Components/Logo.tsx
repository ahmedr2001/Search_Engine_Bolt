import useSearchParamQuery from "../hooks/useSearchParamQuery";

export default function Logo() {
	const [, isSeeingResults, setQuery] = useSearchParamQuery();
	return (
		<h1
			className={` font-bold mx-auto text-secondary cursor-pointer transition-all ${
				isSeeingResults() ? "text-3xl" : "text-6xl"
			}`}
			onClick={() => setQuery("")}>
			BOLT
		</h1>
	);
}
