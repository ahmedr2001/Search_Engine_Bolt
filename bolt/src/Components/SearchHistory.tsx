import getSearchHistory from "../utils/getSearchHistory";
import { AiFillCloseCircle } from "react-icons/ai";
import removeSearchQueryFromHistory from "../utils/removeSearchQueryFromHistory";
import { useState } from "react";
import useSearchParamQuery from "../hooks/useSearchParamQuery";
import { historyItem } from "../App";
type Props = {
	filter: string;
	setText: React.Dispatch<React.SetStateAction<string>>;
	setIsMouseInside: React.Dispatch<React.SetStateAction<boolean>>;
	inputRef: React.RefObject<HTMLInputElement>;
	searchHistoryList: historyItem[];
};

export default function SearchHistory({
	filter,
	setText,
	inputRef,
	setIsMouseInside,
	searchHistoryList,
}: Props) {
	/* const [searchHistory, setSearchHistory] = useState(getSearchHistory()); */
	const [query, isSeeingResults, setQuery] = useSearchParamQuery();
	const filteredSearchHistory = searchHistoryList
		.filter((item) => item.body.toLowerCase().startsWith(filter))
		.slice(0, 7);
	if (filteredSearchHistory.length != 0)
		return (
			<ul
				className={` absolute left-0 right-0  transition-all flex flex-col py-2 top-20 mx-auto border-2 rounded-lg border-res-border bg-res-bg text-res-color ${
					isSeeingResults() ? "w-10/12" : "w-6/12"
				}`}
				onMouseEnter={() => setIsMouseInside(true)}
				onMouseLeave={() => setIsMouseInside(false)}>
				{filteredSearchHistory.map((item, index) => (
					<li
						key={index}
						className=" justify-between px-6 mx-2 flex-grow hover:bg-primary rounded-full transition-all flex flex-row items-center ">
						<span
							className="py-2 flex-grow  cursor-pointer rounded-full  transition-all flex flex-row items-center "
							onClick={() => {
								setText(item.body);
								inputRef.current?.focus();
							}}>
							{item.body}
						</span>
					</li>
				))}
			</ul>
		);
	else return <></>;
}
