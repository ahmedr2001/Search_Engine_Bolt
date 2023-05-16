import React from "react";
import resultType from "../types/resultType";
import useSearchParamQuery from "../hooks/useSearchParamQuery";
import { useEffect } from "react";

type Props = {
	res: resultType;
	paragraph: string;
};

export default function Result({ res, paragraph }: Props) {
	const [query, isSeeingResults, setQuery] = useSearchParamQuery();
	let wordIndexes: number[] = [];

	const isH: (x: string) => boolean = (wordP: string) => {
		const newQuery = query.replaceAll('"', " ").toLowerCase().split(" ");
		for (let i = 0; i < newQuery.length; i++) {
			if (newQuery[i] == '"' || newQuery[i] == "") continue;
			if (wordP.toLowerCase().startsWith(newQuery[i].toLowerCase()))
				return true;
		}
		return false;
	};
	return (
		<div className=" flex flex-col gap-1 items-start">
			<a href={res.url}>
				<h3 className=" font-bold text-lg hover:underline text-highlight">
					{res.title == "" ? res.url : res.title}
				</h3>
			</a>
			<a className=" text-neutral-500 " href={res.url}>
				{res.url}
			</a>
			{paragraph && (
				<p className=" text-neutral-600 ">
					{paragraph.split(" ").map((word: string, index) => {
						if (isH(word)) {
							return (
								<span className="text-xl font-bold" key={index}>
									{" "}
									{word}{" "}
								</span>
							);
						}
						return (
							<span className="text-neutral-600" key={index}>
								{" "}
								{word}{" "}
							</span>
						);
					})}
				</p>
			)}
		</div>
	);
}
