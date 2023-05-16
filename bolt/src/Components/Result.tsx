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

	return (
		<div className=" flex flex-col gap-1 items-start">
			<a href={res.url}>
				<h3 className=" font-bold text-lg hover:underline text-highlight">
					{res.title}
				</h3>
			</a>
			<a className=" text-neutral-500 " href={res.url}>
				{res.url}
			</a>
			{paragraph && (
				<p className=" text-neutral-600 ">
					{paragraph.split(" ").map((word, index) => {
						if (
							query
								.replaceAll('"', " ")
								.toLowerCase()
								.split(" ")
								.includes(word.toLowerCase())
						) {
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
